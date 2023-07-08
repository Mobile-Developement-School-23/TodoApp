package ru.myitschool.todo.ui.addition_fragment

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.myitschool.todo.App
import ru.myitschool.todo.R
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.databinding.FragmentAdditionBinding
import ru.myitschool.todo.ui.ViewModelFactory
import java.util.Calendar
import java.util.Locale

class AdditionFragment : Fragment() {
    private var _binding: FragmentAdditionBinding? = null
    private val binding get() = _binding!!
    private val navController: NavController by lazy {
        NavHostFragment.findNavController(this)
    }
    private val viewModel: AdditionViewModel by viewModels{
        ViewModelFactory{
            (requireActivity().application as App).getAppComponent().additionFragmentComponent().additionViewModel()
        }
    }
    private val errorToast: Toast by lazy {
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.addition_error),
            Toast.LENGTH_SHORT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdditionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        binding.deleteButton.isEnabled = false
        binding.todoEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

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
            if (!binding.close.isEnabled){
                Snackbar.make(binding.deleteButton, resources.getString(R.string.save), Snackbar.LENGTH_SHORT).show()
            }
            if (binding.todoEditText.text.isEmpty()) {
                errorToast.show()
            } else {
                binding.close.isEnabled = false
                lifecycleScope.launch {
                    viewModel.saveCase().collect {
                        if (it) {
                            navController.popBackStack()
                        }
                    }
                }
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
    private fun observeViewModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isDeleted.collect {
                    if (it) {
                        navController.popBackStack()
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.text.collect {
                    if (it != binding.todoEditText.text.toString()) {
                        binding.todoEditText.setText(it)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.priority.collect {
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
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deadlineDate.collect {
                    if (it != null) {
                        binding.deadlineSwitcher.isChecked = true
                        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
                        binding.deadlineTextview.text = dateFormat.format(it)
                        binding.deadlineTextview.visibility = View.VISIBLE
                    } else {
                        binding.deadlineTextview.visibility = View.INVISIBLE
                    }
                }
            }
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
            val date = Calendar.getInstance()
            date.set(year,month,dayOfMonth)
            viewModel.setDeadline(date.time)
        }
        datePicker.setOnCancelListener {
            binding.deadlineSwitcher.isChecked = false
        }
        datePicker.show()
    }

    private fun enableDeleteButton() {
        binding.deleteButton.isEnabled = true
        binding.deleteTextview.setTextColor(
            resources.getColor(
                R.color.red,
                requireContext().theme
            )
        )
        binding.deleteImageview.setColorFilter(
            resources.getColor(
                R.color.red,
                requireContext().theme
            )
        )
    }
}