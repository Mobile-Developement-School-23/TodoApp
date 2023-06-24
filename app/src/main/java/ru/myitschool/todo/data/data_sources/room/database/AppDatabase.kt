package ru.myitschool.todo.data.data_sources.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.myitschool.todo.data.data_sources.room.dao.TodoDao
import ru.myitschool.todo.data.data_sources.room.entities.TodoItemEntity

@Database(entities = [TodoItemEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase(){
    companion object{
        private var db:AppDatabase? = null
        fun getInstance(context:Context):AppDatabase{
            return db?: synchronized(AppDatabase::class){
                db?: Room.databaseBuilder(context, AppDatabase::class.java, "todo.db").build().also {
                    db = it
                }
            }
        }
    }
    abstract fun todoDao(): TodoDao
}