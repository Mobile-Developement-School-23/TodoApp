package ru.myitschool.todo.data.data_sources.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.myitschool.todo.data.data_sources.room.entities.TodoItemEntity
import ru.myitschool.todo.data.models.TodoItem


@Dao
abstract class TodoDao{
    @Query("select * from todo_items")
    abstract fun loadAllTodoItems(): Flow<List<TodoItemEntity>>
    @Insert
    abstract suspend fun insertTodoItem(todoItemEntity: TodoItemEntity)
    @Update
    abstract suspend fun updateTodoItem(todoItemEntity: TodoItemEntity)
    @Query("select * from todo_items where id = :id")
    abstract suspend fun loadTodoItemById(id:String):TodoItemEntity?
    @Query("select * from todo_items where importance = :importance order by changed_at DESC")
    abstract suspend fun loadTodoItemsByImportance(importance:String):List<TodoItemEntity>
    @Query("select * from todo_items order by changed_at DESC")
    abstract suspend fun loadAllTodoItemsAsync():List<TodoItemEntity>
    @Query("delete from todo_items where id = :id")
    abstract suspend fun deleteById(id:String)
    @Query("delete from todo_items")
    abstract suspend fun deleteAll()
    @Insert
    abstract suspend fun insertAll(todoItems:List<TodoItemEntity>)
    @Transaction
    open suspend fun rewriteTable(todoItems:List<TodoItemEntity>){
        deleteAll()
        insertAll(todoItems)
    }
}
