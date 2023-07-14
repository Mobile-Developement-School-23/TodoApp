package ru.myitschool.todo.data.repository

import kotlinx.coroutines.flow.Flow
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem

interface TodoItemsRepository {
    suspend fun addItem(todoItem: TodoItem): Result<Boolean>

    suspend fun getItemById(id: String): Result<TodoItem?>

    suspend fun updateItem(todoItem: TodoItem, withUpdate: Boolean): Result<Boolean>

    suspend fun getItemsByPriority(priority: Priority): Result<List<TodoItem>>

    suspend fun deleteItem(id: String): Result<Boolean>

    suspend fun loadAllItems(): Result<Boolean>

    suspend fun getAllItems(): Result<List<TodoItem>>

    suspend fun updateItems(): Result<Boolean>
    fun getItemsFlow(): Flow<List<TodoItem>>
}