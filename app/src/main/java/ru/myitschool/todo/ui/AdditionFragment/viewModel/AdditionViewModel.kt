package ru.myitschool.todo.ui.AdditionFragment.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem
import ru.myitschool.todo.repository.TodoItemsRepository
import java.util.*

class AdditionViewModel : ViewModel() {
    val priority = MutableLiveData(Priority.NORMAL)
    val text = MutableLiveData("")
    val deadlineDate = MutableLiveData<Date?>()
    val deleted = MutableLiveData<Boolean>()
    private var isCompleted: Boolean? = null
    private var creationDate: Date? = null
    private val repository = TodoItemsRepository()
    private var canDelete = false

    var id: String = "id"
    fun setPriority(value: Priority) {
        priority.value = value
    }

    fun setText(value: String) {
        text.value = value
    }

    fun setDeadline(value: Date?) {
        deadlineDate.value = value
    }

    fun saveCase() {
        val todoItem = TodoItem(
            id = id,
            text = text.value!!,
            priority = priority.value!!,
            isCompleted = if (isCompleted != null) isCompleted!! else false,
            creationDate = if (creationDate != null) creationDate!! else Date(),
            deadline = if (deadlineDate.value != null) deadlineDate.value else null
        )
        if (id == "id") {
            repository.addItem(todoItem)
        } else {
            repository.updateItem(todoItem)
        }

    }

    fun loadTodoItem(id: String) {
        canDelete = true
        val todoItem = repository.getItemById(id)
        viewModelScope.launch {
            todoItem.collect{
                text.value = it?.text
                deadlineDate.value = it?.deadline
                priority.value = it?.priority
                isCompleted = it?.isCompleted
                creationDate = it?.creationDate
            }
        }
        this.id = id
    }
    fun deleteTodoItem(){
        if (canDelete){
            repository.deleteItem(id)
            deleted.value = true
        }
    }
}