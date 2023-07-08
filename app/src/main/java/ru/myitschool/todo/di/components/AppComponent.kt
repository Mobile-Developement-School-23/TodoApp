package ru.myitschool.todo.di.components

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.myitschool.todo.App
import ru.myitschool.todo.NetworkWorker
import ru.myitschool.todo.di.modules.NetworkModule
import ru.myitschool.todo.di.scopes.AppScope
import ru.myitschool.todo.ui.MainActivity
import ru.myitschool.todo.ui.addition_fragment.AdditionViewModel
import ru.myitschool.todo.ui.settings_fragment.view.SettingsViewModel
import ru.myitschool.todo.ui.todo_list_fragment.TodoListViewModel

@Component(modules = [NetworkModule::class])
@AppScope
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context
        ): AppComponent
    }

    fun todolistFragmentComponentFactory(): TodolistFragmentComponent.Factory
    fun settingsFragmentComponent(): SettingsFragmentComponent
    fun additionFragmentComponent(): AdditionFragmentComponent
    fun inject(worker: NetworkWorker)
    fun inject(app: App)
    fun inject(activity: MainActivity)
}
