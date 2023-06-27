package ru.myitschool.todo.ui.todo_list_fragment.view

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ItemTouchHelper
import kotlinx.coroutines.launch
import ru.myitschool.todo.R
import ru.myitschool.todo.data.models.TodoItem
import ru.myitschool.todo.databinding.FragmentTodoListBinding
import ru.myitschool.todo.ui.adapters.CounterCallback
import ru.myitschool.todo.ui.adapters.SelectedCallback
import ru.myitschool.todo.ui.adapters.TodoListAdapter
import ru.myitschool.todo.ui.todo_list_fragment.view.recycler.ItemTouchHelperCallback
import ru.myitschool.todo.ui.todo_list_fragment.view_model.TodoListViewModel


class TodoListFragment : Fragment(), SelectedCallback, CounterCallback {

    private var _binding: FragmentTodoListBinding? = null
    private val binding get() = _binding!!
    private val navController: NavController by lazy {
        NavHostFragment.findNavController(this)
    }
    private var scrolled: Boolean = false
    private val viewModel: TodoListViewModel by viewModels()
    private val adapter: TodoListAdapter by lazy {
        TodoListAdapter(viewModel, this, this)
    }
    private var isHidden = false
    private var fabPosition: Float = 0F

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
        binding.settings.setOnClickListener {
            navController.navigate(R.id.action_todoListFragment_to_settingsFragment)
        }
        fabPosition = binding.addCase.translationY
        binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (-verticalOffset == appBarLayout.totalScrollRange) {
                viewModel.setExpanded(false)
                showToolBar()
            } else {
                viewModel.setExpanded(true)
                closeToolBar()
            }
        }

        binding.swipeRefresh.setColorSchemeResources(R.color.blue)

        binding.filterTextview.setOnClickListener {
            showPopupMenu(it)
        }
        binding.swipeRefresh.setOnRefreshListener {
            lifecycleScope.launch {
                requestUpdateData()
            }
        }
        binding.swipeRefresh.setOnChildScrollUpCallback { _, _ ->
            binding.appBar
            false
        }

        // Настройка recyclerview
        val callback = ItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        binding.todoList.setItemViewCacheSize(adapter.itemCount)
        binding.todoList.adapter = adapter
        binding.todoList.itemAnimator?.removeDuration = 0
        binding.todoList.setOnScrollChangeListener { _, _, _, _, oldScrollY ->
            animateFAB(oldScrollY)
        }
        touchHelper.attachToRecyclerView(binding.todoList)
        lifecycleScope.launch {
            viewModel.todoItems.collect {
                if (it != null && _binding != null) {
                    if (!binding.todoList.isComputingLayout) {
                        if (it.isEmpty()) {
                            binding.emptyInfo.visibility = View.VISIBLE
                        } else {
                            binding.emptyInfo.visibility = View.GONE
                        }
                        adapter.todoList = it
                    }
                }
            }
        }

        binding.addCase.setOnClickListener {
            navController.navigate(R.id.action_todoListFragment_to_additionFragment)
        }

        //Подписывание на обновления
        lifecycleScope.launch {
            viewModel.isExpanded.collect {
                if (!scrolled) {
                    binding.appBar.setExpanded(it)
                    scrolled = true
                }
            }
        }
        lifecycleScope.launch {
            viewModel.filterValue.collect {
                var text: Int = R.string.no
                when (it) {
                    0 -> {
                        text = R.string.no
                    }

                    1 -> {
                        text = R.string.high
                    }

                    2 -> {
                        text = R.string.low
                    }
                }
                binding.filterTextview.setText(text)
            }
        }
        lifecycleScope.launch {
            viewModel.loadedError.collect {
                if (it && _binding != null) {
                    binding.errorLayout.visibility = View.VISIBLE
                }
            }
        }
        binding.retryButton.setOnClickListener {
            requestUpdateData()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.swipeRefresh.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scrolled = false
        _binding = null
    }

    private fun closeToolBar() {
        binding.toolbar.visibility = View.GONE
        binding.toolbar.animate().translationY(-binding.toolbar.bottom.toFloat())
    }

    private fun showToolBar() {
        binding.toolbar.visibility = View.VISIBLE
        binding.toolbar.animate().translationY(0F)
    }

    override fun onSelect(todoItem: TodoItem) {
        val data = Bundle()
        data.putString("id", todoItem.id)
        navController.navigate(R.id.action_todoListFragment_to_additionFragment, data)
    }

    override fun onSwipeStart() {
        binding.swipeRefresh.isEnabled = false
    }

    override fun onSwipeFinish() {
        binding.swipeRefresh.isEnabled = true
    }

    override fun onCount(count: Int) {
        binding.completedTextview.text =
            String.format(resources.getString(R.string.completed_d), count)
    }

    private fun showPopupMenu(v: View) {
        val popupMenu = PopupMenu(requireContext(), v)
        popupMenu.inflate(R.menu.priority_menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.none -> {
                    viewModel.setFilterValue(0)
                    true
                }

                R.id.high -> {
                    viewModel.setFilterValue(1)
                    true
                }

                R.id.low -> {
                    viewModel.setFilterValue(2)
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun animateFAB(oldScrollY: Int) {
        if (oldScrollY < 0 && !isHidden) {
            val animator = ObjectAnimator.ofFloat(
                binding.addCase,
                "translationY", fabPosition, fabPosition - 100, binding.root.height.toFloat()
            )
            animator.interpolator = AccelerateInterpolator()
            animator.duration = 500
            isHidden = true
            animator.start()
        } else if (oldScrollY > 0 && isHidden) {
            val animator = ObjectAnimator.ofFloat(
                binding.addCase,
                "translationY", binding.root.height.toFloat(), fabPosition - 100, fabPosition
            )
            isHidden = false
            animator.interpolator = AccelerateInterpolator()
            animator.duration = 500
            animator.start()
        }
    }

    private fun requestUpdateData() {
        lifecycleScope.launch {
            viewModel.reloadData { error ->
                if (_binding != null) {
                    binding.swipeRefresh.isRefreshing = false
                    if (error == 1) {
                        binding.errorLayout.visibility = View.VISIBLE
                    } else {
                        binding.errorLayout.visibility = View.GONE
                    }
                }
            }
        }
    }
}