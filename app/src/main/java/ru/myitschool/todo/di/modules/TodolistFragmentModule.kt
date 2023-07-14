package ru.myitschool.todo.di.modules

import dagger.Module
import dagger.Provides
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