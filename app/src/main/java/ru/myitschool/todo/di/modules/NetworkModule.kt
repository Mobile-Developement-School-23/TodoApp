package ru.myitschool.todo.di.modules

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
import ru.myitschool.todo.di.scopes.AppScope
import ru.myitschool.todo.utils.Constants
import ru.myitschool.todo.utils.NetworkInterceptor
import java.util.concurrent.TimeUnit

@Module
interface NetworkModule {
    companion object {
        @Provides
        @AppScope
        fun provideTodoRetrofitService(networkInterceptor: NetworkInterceptor): TodoService {
            val okHttpClient = OkHttpClient.Builder().addInterceptor(networkInterceptor)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build()
            return Retrofit.Builder().baseUrl(Constants.TODO_URL).client(okHttpClient)
                .addConverterFactory(
                    GsonConverterFactory.create()
                ).build().create(TodoService::class.java)
        }

        @Provides
        @AppScope
        fun providePassportRetrofitService(): YandexPassportService {
            val okHttpClient = OkHttpClient.Builder().build()
            return Retrofit.Builder().baseUrl(Constants.AUTH_URL).client(okHttpClient)
                .addConverterFactory(
                    GsonConverterFactory.create()
                ).build().create(YandexPassportService::class.java)
        }

        @AppScope
        @Provides
        fun provideSharedPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        }
    }
}