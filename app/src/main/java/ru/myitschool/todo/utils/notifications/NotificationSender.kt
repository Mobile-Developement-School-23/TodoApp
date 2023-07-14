package ru.myitschool.todo.utils.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import ru.myitschool.todo.R
import ru.myitschool.todo.di.components.AppContext
import ru.myitschool.todo.di.scopes.AppScope
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@AppScope
class NotificationSender @Inject constructor(
    @AppContext private val context: Context,
    private val notificationManager: NotificationManager
) {

    private val formatDate = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())

    companion object {
        const val CHANNEL_ID = "ultra_hype_todo"
    }

    init {
        initializeChannel()
    }

    private fun initializeChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.app_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    fun sendNotification(todoText: String, todoDeadline: Date, todoId: String, importance:String) {
        val bundle = Bundle()
        bundle.putString("id", todoId)
        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.additionFragment)
            .setArguments(bundle)
            .createPendingIntent()
        val deadlineIntent =
            Intent(context, SuspendDeadlineNotificationReceiver::class.java)
        deadlineIntent.putExtra("id", todoId)
        val deadlinePendingIntent =
            PendingIntent.getBroadcast(context,
                System.currentTimeMillis().toInt(),
                deadlineIntent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_check)
            .setContentTitle("$importance ${context.getString(R.string.priority).lowercase()}")
            .setSubText(formatDate.format(todoDeadline))
            .setContentText(todoText)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_check, context.getString(R.string.delay), deadlinePendingIntent)
            .build()
        notificationManager.notify(todoId, 1, notification)
    }

    fun cancelNotification(id:String){
        notificationManager.cancel(id, 1)
    }
}