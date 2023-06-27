package ru.myitschool.todo.ui.addition_fragment.view_model

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem
import ru.myitschool.todo.data.repository.TodoItemsRepository
import java.security.MessageDigest
import java.util.Date
import kotlin.random.Random

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
    private var saved = false

    val sharedPreferences = application.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

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

    fun saveCase():StateFlow<Boolean>{
        val isLoadedFlow = MutableStateFlow(false)
        if (!saved) {
            saved = true
            val todoItem = loadedTodoItem?.copy(
                text = text.value,
                priority = priority.value,
                deadline = deadlineDate.value,
                changingDate = Date()
            ) ?: TodoItem(
                id = hashString(Random.nextInt().toString()),
                text = text.value,
                priority = priority.value,
                isCompleted = false,
                creationDate = Date(),
                deadline = deadlineDate.value
            )
            viewModelScope.launch {
                val value = repository.getItemById(todoItem.id)
                if (value == null) {
                    repository.addItem(todoItem)
                } else {
                    repository.updateItem(todoItem, true)
                }
                isLoadedFlow.value = true
            }
        }
        return isLoadedFlow
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
            canDelete = false
            viewModelScope.launch {
                repository.deleteItem(id)
                _isDeleted.value = true
            }
        }
    }
    private fun hashString(str: String): String {
        return MessageDigest.getInstance("sha-256").digest(str.toByteArray())
            .fold("") { string, it -> string + "%02x".format(it) }
    }

}