package ru.myitschool.todo.ui.TodoListFragment.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import ru.myitschool.todo.data.models.TodoItem
import ru.myitschool.todo.repository.TodoItemsRepository

class TodoListViewModel:ViewModel() {
    private val repository:TodoItemsRepository = TodoItemsRepository()
    val isExpanded = MutableLiveData<Boolean>()
    fun setExpanded(value:Boolean){
        isExpanded.value = value
    }
    fun updateItem(todoItem: TodoItem){
        repository.updateItem(todoItem)
    }
    fun getTodoList(): MutableLiveData<MutableList<TodoItem>> {
        return repository.todoItems
    }
    fun deleteItem(id:String){
        repository.deleteItem(id)
    }
}