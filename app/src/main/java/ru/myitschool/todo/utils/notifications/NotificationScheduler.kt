package ru.myitschool.todo.utils.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import ru.myitschool.todo.data.repository.SharedPreferencesRepository
import ru.myitschool.todo.di.components.AppContext
import java.util.Date
import javax.inject.Inject

class NotificationScheduler @Inject constructor(
    private val alarmManager: AlarmManager,
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    @AppContext
    private val context: Context) {
    fun scheduleNotification(id:String, deadline:Date){

        Log.i("NotificationScheduler", "Schedule notification")
        if (sharedPreferencesRepository.readNotificationPermission() == true) {
            val intent = Intent(context, NotificationReceiver::class.java)
            intent.putExtra("id", id)
            val pendingIntent = PendingIntent.getBroadcast(
                context, System.currentTimeMillis().toInt(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            alarmManager.set(AlarmManager.RTC_WAKEUP, deadline.time, pendingIntent)
        }
    }
    fun cancelNotification(id:String){
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("id", id)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
    }
}