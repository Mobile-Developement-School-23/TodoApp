package ru.myitschool.todo.repository

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.*
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem
import java.security.MessageDigest
import java.util.*

class TodoItemsRepository {
    companion object {
        private var created = false
        private val _todoItems = MutableLiveData(mutableListOf<TodoItem>())
    }
    val todoItems = _todoItems
    init {
        if (!created) {
            created = true
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!!", Priority.LOW, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!!!", Priority.NORMAL, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!!!!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!!!!!", Priority.LOW, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!!!!!!", Priority.NORMAL, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!!!!!!!", Priority.HIGH, false, Date()))
        }
    }
    fun addItem(todoItem: TodoItem) {
        val id = hashString(Date().time.toString())
        todoItem.id = id
        todoItems.value?.add(todoItem)
    }

    fun getItemById(id: String): Flow<TodoItem?> {
        val response: Flow<TodoItem?> = flow {
            val data = todoItems.value?.find { it.id == id }
            emit(data)
        }
        return response
    }

    fun updateItem(todoItem: TodoItem) {
        val index = todoItems.value?.withIndex()?.find { it.value.id == todoItem.id }?.index
        if (index != null) {
            todoItems.value?.set(index, todoItem)
        }
    }

    fun deleteItem(id: String) {
        val index = todoItems.value?.withIndex()?.find { it.value.id == id }?.index
        if (index != null) {
            todoItems.value?.removeAt(index)
            todoItems.value = todoItems.value
        }
    }

    private fun hashString(str: String): String {
        return MessageDigest.getInstance("sha-256").digest(str.toByteArray())
            .fold("", { string, it -> string + "%02x".format(it) })
    }

}