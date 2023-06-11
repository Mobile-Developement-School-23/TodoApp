package ru.myitschool.todo.ui.AdditionFragment.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import ru.myitschool.todo.R
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.databinding.FragmentAdditionBinding
import ru.myitschool.todo.ui.AdditionFragment.viewModel.AdditionViewModel

class AdditionFragment : Fragment() {
    private var _binding: FragmentAdditionBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController:NavController
    private lateinit var viewModel: AdditionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdditionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[AdditionViewModel::class.java]
        viewModel.text.observe(viewLifecycleOwner){
            if (it != binding.todoEditText.text.toString()){
                binding.todoEditText.setText(it)
            }
        }
        viewModel.priority.observe(viewLifecycleOwner){
            when(it){
                Priority.LOW->{
                    binding.priorityText.setText(R.string.low)
                }
                Priority.NORMAL->{
                    binding.priorityText.setText(R.string.no)
                }
                Priority.HIGH->{
                    binding.priorityText.setText(R.string.high)
                }
            }
        }
        navController = NavHostFragment.findNavController(this)
        binding.close.setOnClickListener{
            navController.popBackStack()
        }
        binding.save.setOnClickListener{
            navController.popBackStack()
        }
        binding.priority.setOnClickListener{
            showPopupMenu(binding.priorityText)
        }
    }
    private fun showPopupMenu(v:View){
        val popupMenu = PopupMenu(requireContext(), v)
        popupMenu.inflate(R.menu.priority_menu)
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.low->{
                    viewModel.setPriority(Priority.LOW)
                    true
                }
                R.id.none->{
                    viewModel.setPriority(Priority.NORMAL)
                    true
                }
                R.id.high->{
                    viewModel.setPriority(Priority.HIGH)
                    true
                }
                else->false
            }
        }
        popupMenu.show()
    }

}