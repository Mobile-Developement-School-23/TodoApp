package ru.myitschool.todo.ui.addition_fragment

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem
import ru.myitschool.todo.data.repository.TodoItemsRepository
import ru.myitschool.todo.utils.UploadHelper
import java.security.MessageDigest
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

enum class UIError {
    BadSave
}

sealed class UIState {
    object Deleted : UIState()
    object Saved : UIState()
    data class Error(val message: UIError) : UIState()
    object Default : UIState()
}

class AdditionViewModel @Inject constructor(
    private val repository: TodoItemsRepository,
    private val uploadHelper: UploadHelper
) : ViewModel() {
    private val _priority = MutableStateFlow(Priority.NORMAL)
    private val _text = MutableStateFlow("")
    private val _deadlineDate = MutableStateFlow<Date?>(null)
    val priority: StateFlow<Priority> get() = _priority
    val text = _text.asStateFlow()
    val deadlineDate: Flow<String> = _deadlineDate.map {
        if (it == null) {
            return@map ""
        }
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        return@map dateFormat.format(it)
    }
    private var loadedTodoItem: TodoItem? = null
    private var isLoaded = false
    private val _uiState = Channel<UIState>()
    val uiState = _uiState.receiveAsFlow()

    private var canDelete = false
    private var saved = false


    private var id = MutableStateFlow("id")
    val isUpdateScreen: Flow<Boolean> get() = id.map { it != "id" }
    fun setPriority(value: Priority) {
        _priority.value = value
    }

    fun setText(value: String) {
        _text.value = value
    }

    fun setDeadline(value: Date?) {
        _deadlineDate.value = value
    }

    fun saveTodo() {
        if (text.value.isEmpty()) {
            viewModelScope.launch {
                _uiState.send(UIState.Error(UIError.BadSave))
            }
        }
        else if (!saved) {
            saved = true
            val todoItem = loadedTodoItem?.copy(
                text = text.value,
                priority = priority.value,
                deadline = _deadlineDate.value,
                changingDate = Date()
            ) ?: TodoItem(
                id = hashString(Random.nextInt().toString()),
                text = text.value,
                priority = priority.value,
                isCompleted = false,
                creationDate = Date(),
                deadline = _deadlineDate.value
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
                _uiState.send(UIState.Saved)
            }
        }
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
            this.id.value = id
        }
    }

    fun deleteTodoItem() {
        if (canDelete) {
            canDelete = false
            uploadHelper.deleteItem(id.value)
            viewModelScope.launch {
                _uiState.send(UIState.Deleted)
            }
        }
    }

    private fun hashString(str: String): String {
        return MessageDigest.getInstance("sha-256").digest(str.toByteArray())
            .fold("") { string, hs -> string + "%02x".format(hs) }
    }
    fun setDefaultUIState(){
        viewModelScope.launch{
            _uiState.send(UIState.Default)
        }
    }
}