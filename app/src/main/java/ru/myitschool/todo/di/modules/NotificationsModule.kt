package ru.myitschool.todo.di.modules

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.myitschool.todo.di.components.AppContext

@Module
interface NotificationsModule {
    companion object{
        @Provides
        fun provideAlarmManager(@AppContext context: Context):AlarmManager{
            return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }
        @Provides
        fun provideNotificationManager(@AppContext context: Context):NotificationManager{
            return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
    }
}