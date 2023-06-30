package ru.myitschool.todo.data.data_sources.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.myitschool.todo.data.data_sources.room.dao.TodoDao
import ru.myitschool.todo.data.data_sources.room.entities.TodoItemEntity
import ru.myitschool.todo.di.scopes.AppScope

@Database(entities = [TodoItemEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase(){
    abstract fun todoDao(): TodoDao
}