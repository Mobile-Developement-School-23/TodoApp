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
import ru.myitschool.todo.ui.adapters.ItemChanger
import javax.inject.Inject

class TodoListViewModel @Inject constructor(private val repository: TodoItemsRepository) : ViewModel(), ItemChanger {
    companion object{
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
    private val _loadedError = MutableStateFlow(false)
    val loadedError: StateFlow<Boolean> get() = _loadedError


    init {
        viewModelScope.launch {
            repository.todoItems.collect {
                setFilterValue(filterValue.value)
            }
        }
        reloadData()
    }

    fun getItemsByPriority(priority: Priority) {
        viewModelScope.launch(Dispatchers.IO) {
            _todoItems.value = repository.getItemsByPriority(priority)
        }
    }

    fun getItems() {
        viewModelScope.launch(Dispatchers.IO){
            _todoItems.value = repository.getAllItems()
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
    fun reloadData(callback:(error:Int)->Unit = {}) {
        viewModelScope.launch (Dispatchers.IO){
            repository.loadAllItems{
                launch(Dispatchers.Main) {
                    if (it) {
                        callback(0)
                    } else {
                        callback(1)
                    }
                }
            }
        }
    }

    override fun updateItem(todoItem: TodoItem, toTop: Boolean) {
        viewModelScope.launch (Dispatchers.IO){
            repository.updateItem(todoItem, toTop)
        }
    }

    override fun deleteItem(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(id)
        }
    }
}