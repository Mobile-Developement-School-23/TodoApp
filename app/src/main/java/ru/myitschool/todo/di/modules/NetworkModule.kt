package ru.myitschool.todo.di.modules

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.myitschool.todo.data.data_sources.AuthInterceptor
import ru.myitschool.todo.data.data_sources.network.api.TodoService
import ru.myitschool.todo.data.data_sources.room.database.AppDatabase
import ru.myitschool.todo.di.scopes.AppScope
import javax.inject.Inject

@Module
class NetworkModule {
    @Provides
    @AppScope
    fun provideTodoRetrofitService(): TodoService {
        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        return Retrofit.Builder().baseUrl(TodoService.BASE_URL).client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            ).build().create(TodoService::class.java)
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