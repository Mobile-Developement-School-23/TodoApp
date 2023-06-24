package ru.myitschool.todo.repository

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem
import java.security.MessageDigest
import java.util.Date
import java.util.Random

object TodoItemsRepository {
    private val _todoItems = MutableStateFlow(mutableListOf<TodoItem>())

    val todoItems: StateFlow<MutableList<TodoItem>> = _todoItems

    init {
        addItem(
            TodoItem(
                "1",
                "Сделать курсовую!Сделать курсовую!Сделать курсовую!Сделать курсовую!Сделать курсовую!Сделать курсовую!Сделать курсовую!Сделать курсовую!Сделать курсовую!Сделать курсовую!Сделать курсовую!Сделать курсовую!Сделать курсовую!Сделать курсовую!",
                Priority.HIGH,
                true,
                Date(),
                deadline = Date()
            )
        )
        addItem(TodoItem("1", "Сделать курсовую!!", Priority.LOW, false, Date()))
        addItem(TodoItem("1", "Сделать курсовую!!!", Priority.NORMAL, false, Date()))
        addItem(TodoItem("1", "Сделать курсовую!!!!", Priority.HIGH, false, Date()))
        addItem(TodoItem("1", "Сделать курсовую!!!!!", Priority.LOW, false, Date()))
        addItem(TodoItem("1", "Сделать курсовую!!!!!!", Priority.NORMAL, false, Date()))
        addItem(
            TodoItem(
                "1",
                "Сделать курсовую!!!!!!!",
                Priority.HIGH,
                false,
                Date(),
                deadline = Date()
            )
        )
        addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
        addItem(TodoItem("1", "Сделать курсовую!!", Priority.LOW, false, Date()))
        addItem(
            TodoItem(
                "1",
                "Сделать курсовую!!!",
                Priority.NORMAL,
                true,
                Date(),
                deadline = Date(),
                changingDate = Date()
            )
        )
        addItem(TodoItem("1", "Сделать курсовую!!!!", Priority.HIGH, false, Date()))
        addItem(TodoItem("1", "Сделать курсовую!!!!!", Priority.LOW, false, Date()))
        addItem(TodoItem("1", "Сделать курсовую!!!!!!", Priority.NORMAL, true, Date()))
        addItem(
            TodoItem(
                "1",
                "Сделать курсовую!!!!!!!",
                Priority.HIGH,
                false,
                Date(),
                deadline = Date()
            )
        )
        addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
        addItem(TodoItem("1", "Сделать курсовую!!", Priority.LOW, false, Date()))
        addItem(TodoItem("1", "Сделать курсовую!!!", Priority.NORMAL, false, Date()))
        addItem(
            TodoItem(
                "1",
                "Сделать курсовую!!!!",
                Priority.HIGH,
                false,
                Date(),
                deadline = Date(),
            )
        )
        addItem(TodoItem("1", "Сделать курсовую!!!!!", Priority.LOW, false, Date()))
        addItem(TodoItem("1", "Сделать курсовую!!!!!!", Priority.NORMAL, false, Date()))
        addItem(
            TodoItem(
                "1",
                "Сделать курсовую!!!!!!!",
                Priority.HIGH,
                false,
                Date(),
                changingDate = Date()
            )
        )
    }

    fun addItem(todoItem: TodoItem) {
        val id = hashString(Random().nextInt().toString())
        val newTodoItem = todoItem.copy(id = id, changingDate = Date())
        val newValue = todoItems.value.toMutableList()
        newValue.add(0, newTodoItem)
        _todoItems.value = newValue
    }

    suspend fun getItemById(id: String): TodoItem? = coroutineScope {
        todoItems.value.find { it.id == id }
    }

    fun updateItem(todoItem: TodoItem, useSort: Boolean) {
        val index = todoItems.value.withIndex().find { it.value.id == todoItem.id }?.index
        if (index != null && todoItem != todoItems.value[index]) {
            val newValue = todoItems.value.toMutableList()
            if (todoItem == newValue[index]) {
                return
            }
            var updatedTodoItem = todoItem.copy()
            if (useSort) {
                updatedTodoItem = todoItem.copy(changingDate = Date())
            }
            newValue[index] = updatedTodoItem
            if (useSort) {
                val n = newValue.sortedBy { (it.changingDate?.time ?: Long.MAX_VALUE) * -1 }
                _todoItems.value = n.toMutableList()
            } else {
                _todoItems.value = newValue
            }
        }
    }

    suspend fun getItemsByPriority(priority: Priority): List<TodoItem> = coroutineScope {
        todoItems.value.filter { it.priority == priority }
    }

    fun deleteItem(id: String) {
        val index = todoItems.value.withIndex().find { it.value.id == id }?.index
        if (index != null) {
            val newValue = todoItems.value.toMutableList()
            newValue.removeAt(index)
            _todoItems.value = newValue
        }
    }
    suspend fun getAllItems():List<TodoItem> = coroutineScope{
        todoItems.value
    }


    private fun hashString(str: String): String {
        return MessageDigest.getInstance("sha-256").digest(str.toByteArray())
            .fold("") { string, it -> string + "%02x".format(it) }
    }

}