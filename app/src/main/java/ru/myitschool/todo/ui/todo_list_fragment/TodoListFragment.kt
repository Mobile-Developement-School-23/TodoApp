package ru.myitschool.todo.ui.todo_list_fragment

import android.animation.ObjectAnimator
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.myitschool.todo.App
import ru.myitschool.todo.R
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem
import ru.myitschool.todo.databinding.FragmentTodoListBinding
import ru.myitschool.todo.di.components.TodolistFragmentComponent
import ru.myitschool.todo.ui.todo_list_fragment.recycler.CounterCallback
import ru.myitschool.todo.ui.todo_list_fragment.recycler.SelectedCallback
import ru.myitschool.todo.ui.todo_list_fragment.recycler.TodoListAdapter
import ru.myitschool.todo.ui.todo_list_fragment.recycler.ItemTouchHelperCallback
import ru.myitschool.todo.ui.ViewModelFactory
import ru.myitschool.todo.ui.todo_list_fragment.recycler.ItemChanger
import ru.myitschool.todo.ui.todo_list_fragment.recycler.OnCurrentListChangedListener
import ru.myitschool.todo.ui.todo_list_fragment.recycler.TodoItemDecoration
import ru.myitschool.todo.utils.getStringPriority
import javax.inject.Inject


class TodoListFragment : Fragment(), SelectedCallback, CounterCallback {
    companion object {
        private const val DELETE_ANIMATION_DURATION = 40L
        private const val FAB_ANIMATION_DURATION = 500L
    }

    private var _binding: FragmentTodoListBinding? = null
    private val binding get() = _binding!!
    private val navController: NavController by lazy {
        NavHostFragment.findNavController(this)
    }
    private val component: TodolistFragmentComponent by lazy {
        (requireActivity().application as App).getAppComponent().todolistFragmentComponentFactory()
            .create(this)
    }
    private val viewModel: TodoListViewModel by viewModels {
        ViewModelFactory {
            component.todoListViewModel()
        }
    }

    @Inject
    lateinit var adapter: TodoListAdapter
    private var isHidden = false
    private var fabPosition: Float = 0F
    private var scrolled: Boolean = false
    private lateinit var timerSnackBar:Snackbar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root

    }

    fun getItemChanger(): ItemChanger = viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTimer()
        component.inject(this)
        fabPosition = binding.addCase.translationY
        binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (-verticalOffset == appBarLayout.totalScrollRange) {
                viewModel.setExpanded(false)
                showToolBar()
            } else {
                viewModel.setExpanded(true)
                closeToolBar()
                animateFAB(1)
            }
        }

        swipeLayoutSetup()
        todoListSetup()

        binding.addCase.setOnClickListener {
            navController.navigate(R.id.action_todoListFragment_to_additionFragment)
        }
        binding.filterTextview.setOnClickListener {
            showPopupMenu(it)
        }
        binding.settings.setOnClickListener {
            navController.navigate(R.id.action_todoListFragment_to_settingsFragment)
        }
        observeViewModel()
    }

    private fun setupTimer(){
        timerSnackBar = Snackbar.make(binding.addCase, "0", Snackbar.LENGTH_INDEFINITE)
        timerSnackBar.setAction("Отменить"){
            viewModel.cancelDeleting()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.deleteTimer.collect{
                    if (!timerSnackBar.isShown){
                        timerSnackBar.setText(it.toString()).show()
                    }
                    if (it > 0){
                        timerSnackBar.setText(it.toString())
                    }
                    else{
                        timerSnackBar.dismiss()
                    }
                }
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isExpanded.collect {
                    if (!scrolled) {
                        binding.appBar.setExpanded(it)
                        scrolled = true
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filterValue.collect {
                    val text: String = getStringPriority(requireContext(), it)
                    binding.filterTextview.text = text
                }
            }
        }
    }

    private fun todoListSetup() {
        val callback = ItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        binding.todoList.setItemViewCacheSize(adapter.itemCount)
        binding.todoList.itemAnimator?.removeDuration = DELETE_ANIMATION_DURATION
        binding.todoList.adapter = adapter
        binding.todoList.addItemDecoration(
            TodoItemDecoration(
                bottomOffset = 4f.toPx.toInt(),
                leftOffset = 2f.toPx.toInt(),
                rightOffset = 2f.toPx.toInt()
            )
        )
        adapter.setOnCurrentListChangedListener(object : OnCurrentListChangedListener {
            override fun <T> onCurrentListChanged(
                previous: MutableList<T>,
                current: MutableList<T>
            ) {
                if (previous.isNotEmpty()) {
                    val previousElement = previous[0]
                    if (current.isNotEmpty()) {
                        if (previousElement != current[0]) {
                            binding.todoList.scrollToPosition(0)
                        }
                    }
                }
            }
        })
        binding.todoList.setOnScrollChangeListener { _, _, _, _, oldScrollY ->
            animateFAB(oldScrollY)
        }
        touchHelper.attachToRecyclerView(binding.todoList)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.todoItems.collect {
                    if (it != null) {
                        if (!binding.todoList.isComputingLayout) {
                            if (it.isEmpty()) {
                                binding.emptyInfo.visibility = View.VISIBLE
                            } else {
                                binding.emptyInfo.visibility = View.GONE
                            }
                            adapter.submitList(it)
                        }
                    }
                }
            }
        }
    }

    private fun swipeLayoutSetup() {
        binding.swipeRefresh.setColorSchemeResources(R.color.blue)
        binding.swipeRefresh.setOnRefreshListener {
            lifecycleScope.launch {
                requestUpdateData()
            }
        }
        binding.swipeRefresh.setOnChildScrollUpCallback { _, _ ->
            binding.appBar
            false
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
                    viewModel.setFilterValue(Priority.NORMAL)
                    true
                }

                R.id.high -> {
                    viewModel.setFilterValue(Priority.HIGH)
                    true
                }

                R.id.low -> {
                    viewModel.setFilterValue(Priority.LOW)
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun animateFAB(oldScrollY: Int) {
        var animator = ObjectAnimator()
        var changed = false
        if (oldScrollY < 0 && !isHidden) {
            animator = ObjectAnimator.ofFloat(
                binding.addCase,
                "translationY", fabPosition, fabPosition - 100, binding.root.height.toFloat()
            )
            isHidden = true
            changed = true
        } else if (oldScrollY > 0 && isHidden) {
            animator = ObjectAnimator.ofFloat(
                binding.addCase,
                "translationY", binding.root.height.toFloat(), fabPosition - 100, fabPosition
            )
            isHidden = false
            changed = true
        }
        if (changed) {
            animator.interpolator = AccelerateInterpolator()
            animator.duration = FAB_ANIMATION_DURATION
            animator.start()
        }
    }

    private fun requestUpdateData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.reloadData { error ->
                    binding.swipeRefresh.isRefreshing = false
                    if (error == 0) {
                        Snackbar.make(
                            binding.addCase,
                            resources.getString(R.string.no_connection),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}

val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )