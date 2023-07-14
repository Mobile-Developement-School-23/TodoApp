package ru.myitschool.todo.di.components

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.myitschool.todo.App
import ru.myitschool.todo.NetworkWorker
import ru.myitschool.todo.di.modules.NetworkModule
import ru.myitschool.todo.di.modules.RepositoryModule
import ru.myitschool.todo.di.modules.RoomModule
import ru.myitschool.todo.di.modules.NotificationsModule
import ru.myitschool.todo.di.scopes.AppScope
import ru.myitschool.todo.ui.activity.MainActivity
import ru.myitschool.todo.ui.activity.MainActivityViewModel
import ru.myitschool.todo.utils.notifications.NotificationReceiver
import ru.myitschool.todo.utils.notifications.SuspendDeadlineNotificationReceiver
import javax.inject.Qualifier


@Qualifier
annotation class AppContext

@Component(modules = [NetworkModule::class, RepositoryModule::class, RoomModule::class,NotificationsModule::class])
@AppScope
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(
            @AppContext
            @BindsInstance
            context: Context
        ): AppComponent
    }

    fun todolistFragmentComponentFactory(): TodolistFragmentComponent.Factory
    fun settingsFragmentComponent(): SettingsFragmentComponent
    fun additionFragmentComponent(): AdditionFragmentComponent
    fun inject(worker: NetworkWorker)
    fun inject(app: App)
    fun inject(activity: MainActivity)
    fun inject(activity: NotificationReceiver)
    fun inject(activity: SuspendDeadlineNotificationReceiver)
    fun mainActivityViewModel(): MainActivityViewModel
}
