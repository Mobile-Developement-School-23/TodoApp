package ru.myitschool.todo.data.data_sources.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.myitschool.todo.data.data_sources.room.dao.TodoAddDao
import ru.myitschool.todo.data.data_sources.room.dao.TodoDao
import ru.myitschool.todo.data.data_sources.room.dao.TodoDeleteDao
import ru.myitschool.todo.data.data_sources.room.entities.TodoAddEntity
import ru.myitschool.todo.data.data_sources.room.entities.TodoDeleteEntity
import ru.myitschool.todo.data.data_sources.room.entities.TodoItemEntity

@Database(
    entities = [TodoItemEntity::class, TodoDeleteEntity::class, TodoAddEntity::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun deleteDao(): TodoDeleteDao
    abstract fun addDao():TodoAddDao
}