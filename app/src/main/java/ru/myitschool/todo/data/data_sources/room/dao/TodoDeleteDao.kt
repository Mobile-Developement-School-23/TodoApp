package ru.myitschool.todo.data.data_sources.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.myitschool.todo.data.data_sources.room.entities.TodoDeleteEntity

@Dao
interface TodoDeleteDao {
    @Query("delete from todo_delete")
    suspend fun deleteAll()
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDeleteItem(todoDeleteEntity: TodoDeleteEntity)

    @Query("select * from todo_delete")
    suspend fun getAll():List<TodoDeleteEntity>
}