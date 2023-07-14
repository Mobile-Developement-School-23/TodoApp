package ru.myitschool.todo.di.modules

import dagger.Binds
import dagger.Module
import ru.myitschool.todo.data.repository.SharedPreferencesRepository
import ru.myitschool.todo.data.repository.TodoItemsRepository
import ru.myitschool.todo.data.repository.YandexPassportRepository
import ru.myitschool.todo.data.repository.impl.SharedPreferencesRepositoryImpl
import ru.myitschool.todo.data.repository.impl.TodoItemsRepositoryImpl
import ru.myitschool.todo.data.repository.impl.YandexPassportRepositoryImpl

@Module
interface RepositoryModule {
    @Binds
    fun provideSharedRepository(rep: SharedPreferencesRepositoryImpl): SharedPreferencesRepository

    @Binds
    fun provideTodoItemsRepository(rep:TodoItemsRepositoryImpl): TodoItemsRepository

    @Binds
    fun provideYandexPassportRepository(rep:YandexPassportRepositoryImpl): YandexPassportRepository
}