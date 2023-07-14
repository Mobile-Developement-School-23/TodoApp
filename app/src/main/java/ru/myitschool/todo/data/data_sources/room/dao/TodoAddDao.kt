package ru.myitschool.todo.data.data_sources.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.myitschool.todo.data.data_sources.room.entities.TodoAddEntity

@Dao
interface TodoAddDao {
    @Query("delete from todo_add")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTodo(todoAddEntity: TodoAddEntity)

    @Query("delete from todo_add where id=:id")
    suspend fun deleteById(id: String)

    @Query("select * from todo_add")
    suspend fun getAll():List<TodoAddEntity>
}