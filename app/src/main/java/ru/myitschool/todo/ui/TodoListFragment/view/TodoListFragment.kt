package ru.myitschool.todo.ui.TodoListFragment.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.SimpleItemAnimator
import ru.myitschool.todo.R
import ru.myitschool.todo.databinding.FragmentTodoListBinding
import ru.myitschool.todo.ui.TodoListFragment.view.recycler.ItemTouchHelperCallback
import ru.myitschool.todo.ui.TodoListFragment.viewModel.TodoListViewModel
import ru.myitschool.todo.ui.adapters.TodoListAdapter


class TodoListFragment : Fragment() {

    private var _binding: FragmentTodoListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TodoListAdapter
    private lateinit var navController:NavController
    private var scrolled:Boolean = false
    private lateinit var viewModel:TodoListViewModel

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
        viewModel = ViewModelProvider(this)[TodoListViewModel::class.java]
        navController = NavHostFragment.findNavController(this)
        binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (-verticalOffset == appBarLayout.totalScrollRange){
                viewModel.setExpanded(false)
                showToolBar()
            }
            else{
                viewModel.setExpanded(true)
                closeToolBar()
            }
            lastOffset = verticalOffset
        }

        adapter = TodoListAdapter(viewModel)
        viewModel.getTodoList().observe(viewLifecycleOwner){
            if (it.isEmpty()){
                binding.emptyInfo.visibility = View.VISIBLE
            }
            else{
                binding.emptyInfo.visibility = View.GONE
            }
            adapter.todoList = it
        }
        adapter.checkedCounter.observe(viewLifecycleOwner){
            binding.completedTextview.text = String.format(resources.getString(R.string.completed_d), it)
        }
        val callback = ItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        binding.todoList.setItemViewCacheSize(adapter.itemCount)
        binding.todoList.adapter = adapter
        binding.todoList.itemAnimator?.removeDuration = 0
        touchHelper.attachToRecyclerView(binding.todoList)
        binding.addCase.setOnClickListener{
            navController.navigate(R.id.action_todoListFragment_to_additionFragment)
        }
        adapter.selectedTodoItem.observe(viewLifecycleOwner){
            val data = Bundle()
            data.putString("id", it.id)
            navController.navigate(R.id.action_todoListFragment_to_additionFragment, data)
        }
        viewModel.isExpanded.observe(viewLifecycleOwner){
            if (!scrolled) {
                binding.appBar.setExpanded(it)
                scrolled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scrolled = false
        _binding = null
    }
    private fun closeToolBar(){
        if (binding.toolbar.visibility == View.VISIBLE){
            binding.toolbar.visibility = View.GONE
        }
        binding.toolbar.animate().translationY(-binding.toolbar.bottom.toFloat())
            .setInterpolator(DecelerateInterpolator()).start()
    }
    private fun showToolBar(){
        if (binding.toolbar.visibility == View.GONE){
            binding.toolbar.visibility = View.VISIBLE
        }
        binding.toolbar.animate().translationY(0F).setInterpolator(DecelerateInterpolator()).start()
    }
}