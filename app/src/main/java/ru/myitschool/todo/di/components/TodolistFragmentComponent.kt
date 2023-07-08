package ru.myitschool.todo.di.components

import dagger.BindsInstance
import dagger.Subcomponent
import ru.myitschool.todo.di.modules.TodolistFragmentModule
import ru.myitschool.todo.di.scopes.FragmentScope
import ru.myitschool.todo.ui.todo_list_fragment.TodoListFragment
import ru.myitschool.todo.ui.todo_list_fragment.TodoListViewModel

@FragmentScope
@Subcomponent(modules = [TodolistFragmentModule::class])
interface TodolistFragmentComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance fragment: TodoListFragment
        ): TodolistFragmentComponent
    }

    fun todoListViewModel(): TodoListViewModel
    fun inject(todolistFragment: TodoListFragment)

}