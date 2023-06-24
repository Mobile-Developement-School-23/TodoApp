package ru.myitschool.todo.ui.AdditionFragment.view

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import ru.myitschool.todo.R
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.databinding.FragmentAdditionBinding
import ru.myitschool.todo.ui.AdditionFragment.viewModel.AdditionViewModel
import java.util.*

class AdditionFragment : Fragment() {
    private var _binding: FragmentAdditionBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var viewModel: AdditionViewModel
    private lateinit var errorToast: Toast

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdditionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = NavHostFragment.findNavController(this)
        errorToast = Toast.makeText(
            requireContext(),
            resources.getString(R.string.addition_error),
            Toast.LENGTH_SHORT
        )
        viewModel = ViewModelProvider(this)[AdditionViewModel::class.java]
        viewModel.deleted.observe(viewLifecycleOwner){
            if (it){
                navController.popBackStack()
            }
        }
        viewModel.text.observe(viewLifecycleOwner) {
            if (it != binding.todoEditText.text.toString()) {
                binding.todoEditText.setText(it)
            }
        }
        viewModel.priority.observe(viewLifecycleOwner) {
            when (it) {
                Priority.LOW -> {
                    binding.priorityText.setText(R.string.low)
                }
                Priority.NORMAL -> {
                    binding.priorityText.setText(R.string.no)
                }
                Priority.HIGH -> {
                    binding.priorityText.setText(R.string.high)
                }
            }
        }
        viewModel.deadlineDate.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.deadlineSwitcher.isChecked = true
                val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
                binding.deadlineTextview.text = dateFormat.format(it)
                binding.deadlineTextview.visibility = View.VISIBLE
            } else {
                binding.deadlineTextview.visibility = View.INVISIBLE
            }
        }
        binding.todoEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.setText(binding.todoEditText.text.toString())
            }
        })
        binding.deadlineSwitcher.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (viewModel.deadlineDate.value == null) {
                    showDatePickerDialog()
                }
            } else {
                viewModel.setDeadline(null)
            }
        }
        binding.close.setOnClickListener {
            navController.popBackStack()
        }
        binding.save.setOnClickListener {
            if (binding.todoEditText.text.isEmpty()) {
                errorToast.show()
            } else {
                viewModel.saveCase()
                navController.popBackStack()
            }
        }
        binding.priority.setOnClickListener {
            showPopupMenu(binding.priorityText)
        }
        binding.deleteButton.setOnClickListener {
            viewModel.deleteTodoItem()
        }
        val id = arguments?.getString("id")
        if (id != null) {
            viewModel.loadTodoItem(id)
            enableDeleteButton()
        }
    }

    private fun showPopupMenu(v: View) {
        val popupMenu = PopupMenu(requireContext(), v)
        popupMenu.inflate(R.menu.priority_menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.low -> {
                    viewModel.setPriority(Priority.LOW)
                    true
                }
                R.id.none -> {
                    viewModel.setPriority(Priority.NORMAL)
                    true
                }
                R.id.high -> {
                    viewModel.setPriority(Priority.HIGH)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerDialog(requireContext())
        datePicker.setOnDateSetListener { _, year, month, dayOfMonth ->
            val date = Date(year - 1900, month, dayOfMonth)
            viewModel.setDeadline(date)
        }
        datePicker.setOnCancelListener {
            binding.deadlineSwitcher.isChecked = false
        }
        datePicker.show()
    }

    private fun enableDeleteButton(){
        binding.deleteTextview.setTextColor(
            resources.getColor(
                R.color.red,
                requireContext().theme
            )
        )
        binding.deleteImageview.setColorFilter(resources.getColor(R.color.red, requireContext().theme))
    }
}