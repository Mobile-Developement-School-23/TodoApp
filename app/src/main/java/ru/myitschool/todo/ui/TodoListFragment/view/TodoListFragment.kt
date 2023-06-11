package ru.myitschool.todo.ui.TodoListFragment.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import ru.myitschool.todo.R
import ru.myitschool.todo.databinding.FragmentTodoListBinding
import ru.myitschool.todo.repository.TodoItemsRepository
import ru.myitschool.todo.ui.adapters.TodoListAdapter
import ru.myitschool.todo.ui.recycler.TodoPreviewOffsetItemDecoration

class TodoListFragment : Fragment() {

    private var _binding: FragmentTodoListBinding? = null
    private val repository = TodoItemsRepository()
    private val binding get() = _binding!!
    private lateinit var adapter: TodoListAdapter
    private lateinit var navController:NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TodoListAdapter()
        adapter.todoList = TodoItemsRepository.todoItems
        navController = NavHostFragment.findNavController(this)
        adapter.checkedCounter.observe(viewLifecycleOwner){
            binding.completedTextview.text = String.format(resources.getString(R.string.completed_d), it)
        }
        binding.todoList.setItemViewCacheSize(adapter.itemCount)
        binding.todoList.adapter = adapter
        binding.todoList.addItemDecoration(TodoPreviewOffsetItemDecoration(bottomOffset = 20, topOffset = 20))
        binding.addCase.setOnClickListener{
            navController.navigate(R.id.action_todoListFragment_to_additionFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}