package ru.myitschool.todo.ui.addition_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem
import ru.myitschool.todo.data.repository.TodoItemsRepository
import ru.myitschool.todo.utils.UploadHelper
import java.security.MessageDigest
import java.util.Date
import javax.inject.Inject
import kotlin.random.Random

class AdditionViewModel @Inject constructor(
    private val repository: TodoItemsRepository,
    private val uploadHelper: UploadHelper
) : ViewModel() {
    private val _priority = MutableStateFlow(Priority.NORMAL)
    private val _text = MutableStateFlow("")
    private val _deadlineDate = MutableStateFlow<Date?>(null)
    private val _isDeleted = MutableStateFlow(false)
    val priority: StateFlow<Priority> get() = _priority
    val text: StateFlow<String> get() = _text
    val deadlineDate: StateFlow<Date?> get() = _deadlineDate
    val isDeleted: StateFlow<Boolean> get() = _isDeleted
    private var loadedTodoItem: TodoItem? = null
    private var isLoaded = false

    private var canDelete = false
    private var saved = false


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

    fun saveCase(): StateFlow<Boolean> {
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
            viewModelScope.launch(Dispatchers.IO) {
                var value: TodoItem? = null
                repository.getItemById(todoItem.id).onSuccess {
                    value = it
                }.onFailure {
                    value = null
                }
                if (value == null) {
                    uploadHelper.addItem(todoItem)
                } else {
                    uploadHelper.updateItem(todoItem, true)
                }
                isLoadedFlow.value = true
            }
        }
        return isLoadedFlow
    }

    fun loadTodoItem(id: String) {
        canDelete = true
        if (!isLoaded) {
            isLoaded = true
            viewModelScope.launch {
                var todoItem: TodoItem? = null
                repository.getItemById(id).onSuccess {
                    todoItem = it
                }
                _text.value = todoItem?.text ?: ""
                _deadlineDate.value = todoItem?.deadline
                _priority.value = todoItem?.priority ?: Priority.NORMAL
                launch(Dispatchers.Main) {
                    loadedTodoItem = todoItem
                }
            }
            this.id = id
        }
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
            .fold("") { string, hs -> string + "%02x".format(hs) }
    }

}