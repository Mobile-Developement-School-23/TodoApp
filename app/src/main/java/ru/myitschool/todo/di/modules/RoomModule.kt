package ru.myitschool.todo.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.myitschool.todo.data.data_sources.room.dao.TodoAddDao
import ru.myitschool.todo.data.data_sources.room.dao.TodoDao
import ru.myitschool.todo.data.data_sources.room.dao.TodoDeleteDao
import ru.myitschool.todo.data.data_sources.room.database.AppDatabase
import ru.myitschool.todo.di.scopes.AppScope

@Module
interface RoomModule {
    companion object {
        @AppScope
        @Provides
        fun provideRoomDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "todo.db").build()
        }

        @AppScope
        @Provides
        fun provideTodoAddDao(database: AppDatabase): TodoAddDao {
            return database.addDao()
        }

        @AppScope
        @Provides
        fun provideTodoDao(database: AppDatabase): TodoDao {
            return database.todoDao()
        }

        @AppScope
        @Provides
        fun provideTodoDeleteDao(database: AppDatabase): TodoDeleteDao {
            return database.deleteDao()
        }
    }
}