package ru.myitschool.todo.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.myitschool.todo.data.data_sources.network.todoitems_server.TodoService
import ru.myitschool.todo.data.data_sources.network.yandex_passport.YandexPassportService
import ru.myitschool.todo.data.data_sources.room.database.AppDatabase
import javax.inject.Inject

@Module
class NetworkModule {
    @Provides
    @AppScope
    fun provideTodoRetrofitService(): TodoService {
        val okHttpClient = OkHttpClient.Builder().build()
        return Retrofit.Builder().baseUrl(TodoService.BASE_URL).client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            ).build().create(TodoService::class.java)
    }
    @Provides
    @AppScope
    fun providePassportRetrofitService(): YandexPassportService {
        val okHttpClient = OkHttpClient.Builder().build()
        return Retrofit.Builder().baseUrl(YandexPassportService.BASE_URL).client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            ).build().create(YandexPassportService::class.java)
    }
    @Provides
    @Inject
    fun provideSharedPreferences(context:Context):SharedPreferences{
        return context.getSharedPreferences("AppSettings",Context.MODE_PRIVATE)
    }

    @Provides
    @Inject
    fun provideRoomDatabase(context:Context):AppDatabase{
        return Room.databaseBuilder(context, AppDatabase::class.java, "todo.db").build()
    }
}