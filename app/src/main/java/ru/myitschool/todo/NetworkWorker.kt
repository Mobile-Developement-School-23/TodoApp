package ru.myitschool.todo

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.runBlocking
import ru.myitschool.todo.App
import ru.myitschool.todo.data.repository.TodoItemsRepository
import java.lang.Exception
import javax.inject.Inject

class NetworkWorker(context: Context, workerParams: WorkerParameters) :
    Worker(
        context,
        workerParams){
    @Inject
    lateinit var repository: TodoItemsRepository

    init {
        (applicationContext as App).getAppComponent().inject(this)
    }
    override fun doWork(): Result {
        return try{
            runBlocking {
                repository.updateItems()
            }
            Result.success()
        } catch (_:Exception) {
            Result.failure()
        }
    }

}