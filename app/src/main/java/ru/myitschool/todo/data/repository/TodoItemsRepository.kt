package ru.myitschool.todo.data.repository

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import ru.myitschool.todo.data.data_sources.room.dao.TodoDao
import ru.myitschool.todo.data.data_sources.room.data_mappers.ImportanceMapper
import ru.myitschool.todo.data.data_sources.room.data_mappers.TodoMapper
import ru.myitschool.todo.data.data_sources.room.database.AppDatabase
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem
import java.security.MessageDigest
import java.util.Date
import java.util.Random

class TodoItemsRepository(val context: Context) {

    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }
    private val todoDao: TodoDao by lazy {
        database.todoDao()
    }
    val todoItems: Flow<List<TodoItem>> = todoDao.loadAllTodoItems().map { list -> list.map { TodoMapper.entityToModel(it) } }


    suspend fun addItem(todoItem: TodoItem) {
        val id = hashString(Random().nextInt().toString())
        val newTodoItem = todoItem.copy(id = id, changingDate = Date())
        todoDao.insertTodoItem(TodoMapper.modelToEntity(newTodoItem))
    }

    suspend fun getItemById(id: String): TodoItem? {
        val todoItemEntity = todoDao.loadTodoItemById(id)
        return if (todoItemEntity == null) {
            null
        } else {
            TodoMapper.entityToModel(todoItemEntity)
        }
    }

    suspend fun updateItem(todoItem: TodoItem, useSort: Boolean) {
        var updateItem = todoItem
        if (useSort) {
            updateItem = todoItem.copy(changingDate = Date())
        }
        todoDao.updateTodoItem(TodoMapper.modelToEntity(updateItem))
    }

    suspend fun getItemsByPriority(priority: Priority): List<TodoItem> {
        return todoDao.
        loadTodoItemsByImportance(ImportanceMapper.priorityToImportance(priority))
            .map { TodoMapper.entityToModel(it) }
    }

    suspend fun deleteItem(id: String) {
        todoDao.deleteById(id)
    }

    suspend fun getAllItems(): List<TodoItem> =
        todoDao.loadAllTodoItemsAsync().map {
            TodoMapper.entityToModel(it)
        }


    private fun hashString(str: String): String {
        return MessageDigest.getInstance("sha-256").digest(str.toByteArray())
            .fold("") { string, it -> string + "%02x".format(it) }
    }

}