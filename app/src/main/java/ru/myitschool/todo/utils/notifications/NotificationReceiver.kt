package ru.myitschool.todo.utils.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.myitschool.todo.App
import ru.myitschool.todo.data.models.TodoItem
import ru.myitschool.todo.data.repository.TodoItemsRepository
import ru.myitschool.todo.utils.getStringPriority
import java.util.Calendar
import javax.inject.Inject

class NotificationReceiver: BroadcastReceiver() {
    @Inject
    lateinit var repository:TodoItemsRepository
    @Inject
    lateinit var notificationSender: NotificationSender
    private val scope = CoroutineScope(Dispatchers.Main.immediate)
    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as App).getAppComponent().inject(this)
        val intentId = intent.getStringExtra("id")
        val id = intentId?:"id"
        Log.i("TODO", "Start notification $id")
        var item: TodoItem? = null
        scope.launch {
            val job = scope.launch(Dispatchers.IO) {
                repository.getItemById(id = id).onSuccess {
                    item = it
                }
            }
            job.join()
            if (item != null){
                val nItem = item!!
                if (nItem.deadline == null){
                    return@launch
                }
                notificationSender.sendNotification(nItem.text, nItem.deadline, nItem.id, getStringPriority(context, nItem.priority))
            }
        }
    }
}
class SuspendDeadlineNotificationReceiver:BroadcastReceiver(){
    @Inject
    lateinit var repository:TodoItemsRepository
    @Inject
    lateinit var notificationSender: NotificationSender

    private val scope = CoroutineScope(Dispatchers.Main.immediate)
    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as App).getAppComponent().inject(this)
        val id = intent.getStringExtra("id")?:"id"
        Log.i("TODO", "Notification delay $id")
        var item: TodoItem? = null
        scope.launch {
            val job = scope.launch(Dispatchers.IO) {
                repository.getItemById(id = id).onSuccess {
                    item = it
                }
            }
            job.join()
            if (item != null && item?.deadline != null){
                val cal = Calendar.getInstance()
                cal.time = item!!.deadline!!
                cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)+1)
                val newItem = item!!.copy(deadline = cal.time)
                repository.updateItem(todoItem = newItem, true)
            }
        }
        notificationSender.cancelNotification(id)
    }

}