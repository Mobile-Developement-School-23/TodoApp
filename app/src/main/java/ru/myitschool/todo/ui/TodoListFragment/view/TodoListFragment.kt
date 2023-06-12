package ru.myitschool.todo.ui.TodoListFragment.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import ru.myitschool.todo.R
import ru.myitschool.todo.databinding.FragmentTodoListBinding
import ru.myitschool.todo.repository.TodoItemsRepository
import ru.myitschool.todo.ui.adapters.TodoListAdapter


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
        var lastOffset = 0
        binding.toolbar.animate().translationY(-binding.toolbar.bottom.toFloat())
            .setInterpolator(DecelerateInterpolator()).start()
        binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (-verticalOffset == appBarLayout.totalScrollRange){
                if (binding.toolbar.visibility == View.GONE){
                    binding.toolbar.visibility = View.VISIBLE
                }
                binding.toolbar.animate().translationY(0F).setInterpolator(DecelerateInterpolator()).start()
            }
            else{
                if (binding.toolbar.visibility == View.VISIBLE){
                    binding.toolbar.visibility = View.GONE
                }
                binding.toolbar.animate().translationY(-binding.toolbar.bottom.toFloat())
                    .setInterpolator(DecelerateInterpolator()).start()
            }
            lastOffset = verticalOffset
        }
        adapter = TodoListAdapter()
        adapter.todoList = TodoItemsRepository.todoItems
        navController = NavHostFragment.findNavController(this)
        adapter.checkedCounter.observe(viewLifecycleOwner){
            binding.completedTextview.text = String.format(resources.getString(R.string.completed_d), it)
        }
        binding.todoList.setItemViewCacheSize(adapter.itemCount)
        binding.todoList.adapter = adapter
        binding.addCase.setOnClickListener{
            navController.navigate(R.id.action_todoListFragment_to_additionFragment)
        }
        adapter.selectedTodoItem.observe(viewLifecycleOwner){
            val data = Bundle()
            data.putString("id", it.id)
            navController.navigate(R.id.action_todoListFragment_to_additionFragment, data)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}