package ru.myitschool.todo.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.myitschool.todo.data.repository.TodoItemsRepository
import ru.myitschool.todo.di.scopes.AppScope
import javax.inject.Inject

@AppScope
class NetworkListener @Inject constructor(
    private val networkStateMonitor: NetworkStateMonitor,
    private val repository: TodoItemsRepository) {
    private val scope = CoroutineScope(Dispatchers.Main)
    private var currentJob: Job? = null
    fun startListener(){
        removeListener()
        currentJob = scope.launch {
            networkStateMonitor.isConnected.collect {
                if (it){
                    repository.updateItems()
                }
            }
        }
    }
    fun removeListener(){
        currentJob?.cancel()
        currentJob = null
    }
}