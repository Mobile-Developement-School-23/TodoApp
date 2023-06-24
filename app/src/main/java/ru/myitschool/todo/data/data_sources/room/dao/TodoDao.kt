package ru.myitschool.todo.data.data_sources.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.myitschool.todo.data.data_sources.room.entities.TodoItemEntity
import ru.myitschool.todo.data.models.TodoItem


@Dao
interface TodoDao{
    @Query("select * from TodoItemEntity order by changed_at DESC")
    fun loadAllTodoItems(): Flow<List<TodoItemEntity>>
    @Insert
    suspend fun insertTodoItem(todoItemEntity: TodoItemEntity)
    @Update
    suspend fun updateTodoItem(todoItemEntity: TodoItemEntity)
    @Query("select * from TodoItemEntity where id = :id")
    suspend fun loadTodoItemById(id:String):TodoItemEntity?
    @Query("select * from TodoItemEntity where importance = :importance")
    suspend fun loadTodoItemsByImportance(importance:String):List<TodoItemEntity>
    @Query("select * from TodoItemEntity order by changed_at DESC")
    suspend fun loadAllTodoItemsAsync():List<TodoItemEntity>
    @Query("delete from TodoItemEntity where id = :id")
    suspend fun deleteById(id:String)
}
