package ru.myitschool.todo.ui.todo_list_fragment.view_model

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.myitschool.todo.data.data_sources.network.data_mappers.TodoNetworkMapper
import ru.myitschool.todo.data.data_sources.network.entities.TodoItemListResponse
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem
import ru.myitschool.todo.data.repository.TodoItemsRepository
import ru.myitschool.todo.ui.adapters.ItemChanger

class TodoListViewModel(application: Application) : AndroidViewModel(application), ItemChanger {
    private val NONE = 0
    private val HIGH = 1
    private val LOW = 2

    private val _filterValue = MutableStateFlow(NONE)
    val filterValue: StateFlow<Int> get() = _filterValue

    private val repository: TodoItemsRepository = TodoItemsRepository(application)

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
        viewModelScope.launch {
            _todoItems.value = repository.getItemsByPriority(priority)
        }
    }

    fun getItems() {
        viewModelScope.launch {
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
        viewModelScope.launch {
            repository.loadAllItems{
                if (it){
                    callback(0)
                }
                else{
                    callback(1)
                }
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