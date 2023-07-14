package ru.myitschool.todo.ui.todo_list_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem
import ru.myitschool.todo.data.repository.TodoItemsRepository
import ru.myitschool.todo.ui.todo_list_fragment.recycler.ItemChanger
import javax.inject.Inject

class TodoListViewModel @Inject constructor(private val repository: TodoItemsRepository) :
    ViewModel(),
    ItemChanger {

    private val _filterValue = MutableStateFlow(Priority.NORMAL)
    val filterValue: StateFlow<Priority> get() = _filterValue

    private val _todoItems: MutableStateFlow<List<TodoItem>?> =
        MutableStateFlow(null)
    val todoItems: StateFlow<List<TodoItem>?> get() = _todoItems

    private val _isExpanded = MutableStateFlow(true)
    val isExpanded: StateFlow<Boolean> get() = _isExpanded

    private val _deleteTimer = MutableStateFlow(0)
    val deleteTimer: StateFlow<Int> get() = _deleteTimer
    private var savedTodoItem: TodoItem? = null
    private var isDeleting = false


    init {
        viewModelScope.launch {
            repository.getItemsFlow().collect {
                setFilterValue(filterValue.value)
            }
        }
        reloadData()
    }

    fun getItemsByPriority(priority: Priority) {
        viewModelScope.launch {
            repository.getItemsByPriority(priority).onSuccess {
                _todoItems.value = it
            }
        }
    }

    fun getItems() {
        viewModelScope.launch {
            repository.getAllItems().onSuccess {
                _todoItems.value = it
            }
        }
    }

    fun setExpanded(value: Boolean) {
        _isExpanded.value = value
    }

    fun setFilterValue(value: Priority) {
        _filterValue.value = value
        when (value) {
            Priority.NORMAL -> {
                getItems()
            }

            else -> {
                getItemsByPriority(value)
            }
        }
    }

    fun reloadData(callback: (error: Int) -> Unit = {}) {
        viewModelScope.launch {
            repository.updateItems().onSuccess {
                if (it) {
                    callback(1)
                } else {
                    callback(0)
                }
            }.onFailure {
                callback(0)
            }
        }
    }

    override fun updateItem(todoItem: TodoItem, toTop: Boolean) {
        viewModelScope.launch {
            repository.updateItem(todoItem, toTop)
        }
    }

    override fun deleteItem(todoItem: TodoItem) {
        viewModelScope.launch {
            repository.deleteItem(todoItem.id)
        }
        isDeleting = true
        viewModelScope.launch {
            savedTodoItem = todoItem
            for (i in 5 downTo 0) {
                if (!isDeleting || (savedTodoItem != null && todoItem.id != savedTodoItem!!.id)) {
                    break
                }
                _deleteTimer.value = i
                delay(1000)
            }
        }
    }

    fun cancelDeleting() {
        isDeleting = false
        if (savedTodoItem != null) {
            viewModelScope.launch {
                repository.addItem(savedTodoItem!!)
            }
            _deleteTimer.value = 0
        }
    }
}