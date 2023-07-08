package ru.myitschool.todo.di.modules

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import ru.myitschool.todo.di.components.AppComponent
import ru.myitschool.todo.di.scopes.FragmentScope
import ru.myitschool.todo.ui.todo_list_fragment.TodoListFragment
import ru.myitschool.todo.ui.todo_list_fragment.recycler.TodoListAdapter

@Module
interface TodolistFragmentModule {

    companion object {
        @FragmentScope
        @Provides
        fun provideAdapter(fragment: TodoListFragment): TodoListAdapter =
            TodoListAdapter(fragment.getItemChanger(), fragment, fragment)
    }
}