package ru.myitschool.todo.ui.addition_fragment.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem
import ru.myitschool.todo.data.repository.TodoItemsRepository
import java.util.Date

class AdditionViewModel(application: Application) : AndroidViewModel(application) {
    private val _priority = MutableStateFlow(Priority.NORMAL)
    private val _text = MutableStateFlow("")
    private val _deadlineDate = MutableStateFlow<Date?>(null)
    private val _isDeleted = MutableStateFlow(false)
    val priority: StateFlow<Priority> get() = _priority
    val text: StateFlow<String> get() = _text
    val deadlineDate: StateFlow<Date?> get() = _deadlineDate
    val isDeleted: StateFlow<Boolean> get() = _isDeleted
    private var loadedTodoItem: TodoItem? = null
    private val repository = TodoItemsRepository(application)
    private var canDelete = false

    var id: String = "id"
    fun setPriority(value: Priority) {
        _priority.value = value
    }

    fun setText(value: String) {
        _text.value = value
    }

    fun setDeadline(value: Date?) {
        _deadlineDate.value = value
    }

    fun saveCase() {
        val todoItem = loadedTodoItem?.copy(
            text = text.value,
            priority = priority.value,
            deadline = deadlineDate.value
        ) ?: TodoItem(
            id = id,
            text = text.value,
            priority = priority.value,
            isCompleted = false,
            creationDate = Date(),
            deadline = deadlineDate.value
        )
        viewModelScope.launch {
            if (id == "id") {
                repository.addItem(todoItem)
            } else {
                repository.updateItem(todoItem, true)
            }
        }
    }

    fun loadTodoItem(id: String) {
        canDelete = true
        viewModelScope.launch {
            val todoItem = repository.getItemById(id)
            _text.value = todoItem?.text ?: ""
            _deadlineDate.value = todoItem?.deadline
            _priority.value = todoItem?.priority ?: Priority.NORMAL
            loadedTodoItem = todoItem
        }
        this.id = id
    }

    fun deleteTodoItem() {
        if (canDelete) {
            viewModelScope.launch {
                repository.deleteItem(id)
                _isDeleted.value = true
            }
        }
    }
}