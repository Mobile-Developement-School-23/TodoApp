package ru.myitschool.todo.ui.AdditionFragment.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
    fun setPriority(value: Int) {
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
        text.value = todoItem?.text
        deadlineDate.value = todoItem?.deadline
        priority.value = todoItem?.priority
        isCompleted = todoItem?.isCompleted
        creationDate = todoItem?.creationDate
        this.id = id
    }
    fun deleteTodoItem(){
        if (canDelete){
            repository.deleteItem(id)
            deleted.value = true
        }
    }
}