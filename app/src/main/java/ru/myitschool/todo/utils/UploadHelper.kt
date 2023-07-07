package ru.myitschool.todo.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.myitschool.todo.data.models.TodoItem
import ru.myitschool.todo.data.repository.TodoItemsRepository
import ru.myitschool.todo.di.AppScope
import javax.inject.Inject

@AppScope
class UploadHelper @Inject constructor(private val repository: TodoItemsRepository) {
    private val scope = CoroutineScope(Dispatchers.IO)
    fun updateItem(item: TodoItem, update:Boolean) {
        scope.launch {
            repository.updateItem(item, update)
        }
    }
    fun addItem(item:TodoItem){
        scope.launch {
            repository.addItem(item)
        }
    }
}