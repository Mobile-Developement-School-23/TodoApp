package ru.myitschool.todo.ui.todo_list_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
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
    companion object {
        private const val NONE = 0
        private const val HIGH = 1
        private const val LOW = 2
    }

    private val _filterValue = MutableStateFlow(NONE)
    val filterValue: StateFlow<Int> get() = _filterValue

    private val _todoItems: MutableStateFlow<List<TodoItem>?> =
        MutableStateFlow(null)
    val todoItems: StateFlow<List<TodoItem>?> get() = _todoItems

    private val _isExpanded = MutableStateFlow(true)
    val isExpanded: StateFlow<Boolean> get() = _isExpanded


    init {
        viewModelScope.launch {
            repository.todoItems.collect {
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

    fun setFilterValue(value: Int) {
        _filterValue.value = value
        when (value) {
            NONE -> {
                getItems()
            }

            HIGH -> {
                getItemsByPriority(Priority.HIGH)
            }

            LOW -> {
                getItemsByPriority(Priority.LOW)
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

    override fun deleteItem(id: String) {
        viewModelScope.launch {
            repository.deleteItem(id)
        }
    }
}