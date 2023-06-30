package ru.myitschool.todo.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.myitschool.todo.data.data_sources.network.api.TodoService
import ru.myitschool.todo.data.data_sources.network.data_mappers.TodoNetworkMapper
import ru.myitschool.todo.data.data_sources.network.entities.TodoItemListRequest
import ru.myitschool.todo.data.data_sources.network.entities.TodoItemRequest
import ru.myitschool.todo.data.data_sources.room.dao.TodoDao
import ru.myitschool.todo.data.data_sources.room.data_mappers.ImportanceMapper
import ru.myitschool.todo.data.data_sources.room.data_mappers.TodoMapper
import ru.myitschool.todo.data.data_sources.room.database.AppDatabase
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem
import ru.myitschool.todo.di.scopes.AppScope
import java.util.Date
import javax.inject.Inject

@AppScope
class TodoItemsRepository @Inject constructor(
    private val todoService: TodoService,
    private val sharedPrefRepository:SharedPreferencesRepository,
    private val database: AppDatabase
) {

    private val todoDao: TodoDao by lazy {
        database.todoDao()
    }
    val todoItems: Flow<List<TodoItem>> =
        todoDao.loadAllTodoItems().map { list -> list.map { TodoMapper.entityToModel(it) } }



    suspend fun addItem(todoItem: TodoItem) {
        val newTodoItem = todoItem.copy(changingDate = Date())
        todoDao.insertTodoItem(TodoMapper.modelToEntity(newTodoItem))
        val newItem = TodoNetworkMapper.modelToEntity(newTodoItem)
        val requestItem = TodoItemRequest(element = newItem)
        try {
            val response =
                todoService.addTodoItem(sharedPrefRepository.getRevision(), requestItem)
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) {
                    sharedPrefRepository.writeRevision(result.revision)
                }
            }
            else{
                updateItems()
            }
        } catch (_: Exception) {

        }
    }

    suspend fun getItemById(id: String): TodoItem? {
        val todoItemEntity = todoDao.loadTodoItemById(id)
        return if (todoItemEntity == null) {
            null
        } else {
            TodoMapper.entityToModel(todoItemEntity)
        }
    }

    suspend fun updateItem(todoItem: TodoItem, withUpdate: Boolean) {
        var newTodoItem = todoItem
        if (withUpdate) {
            newTodoItem = todoItem.copy(changingDate = Date())
        }
        todoDao.updateTodoItem(TodoMapper.modelToEntity(newTodoItem))
        val newItem = TodoNetworkMapper.modelToEntity(newTodoItem)
        val requestItem = TodoItemRequest(element = newItem)
        try {
            val response = todoService.changeTodoItem(
                sharedPrefRepository.getRevision(),
                newItem.id,
                requestItem
            )
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) {
                    sharedPrefRepository.writeRevision(result.revision)
                }
            }
            else{
                updateItems()
            }
        } catch (_: Exception) {
        }
    }

    suspend fun getItemsByPriority(priority: Priority) =
        todoDao.loadTodoItemsByImportance(ImportanceMapper.priorityToImportance(priority))
            .map { TodoMapper.entityToModel(it) }

    suspend fun deleteItem(id: String) {
        todoDao.deleteById(id)
        try {
            val response = todoService.deleteTodoItem(sharedPrefRepository.getRevision(), id)
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) {
                    sharedPrefRepository.writeRevision(result.revision)
                }
            }
            else{
                updateItems()
            }
        } catch (_: Exception) {

        }
    }

    suspend fun loadAllItems(func: (value: Boolean) -> Unit) {
        try {
            val response = todoService.loadList()
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) {
                    todoDao.rewriteTable(result.list.map {
                        TodoMapper.modelToEntity(
                            TodoNetworkMapper.entityToModel(it)
                        )
                    })
                    sharedPrefRepository.writeRevision(result.revision)
                    func(true)
                } else {
                    func(false)
                }
            } else {
                func(false)
            }
        } catch (e: Exception) {
            func(false)
        }
    }

    suspend fun getAllItems(): List<TodoItem> =
        todoDao.loadAllTodoItemsAsync().map { TodoMapper.entityToModel(it) }
    suspend fun updateItems(){
        try {
            val response = todoService.loadList()
            if (response.isSuccessful){
                val body = response.body()
                if (body != null) {
                    val savedItems = getAllItems().toMutableList()
                    val loadedItems = body.list.toMutableList()
                    for (i in savedItems){ //Тут должна быть рабочая синхронизация, но удаление чет пошло по ж***
                        if (loadedItems.all { it.id != i.id }){
                            loadedItems.add(TodoNetworkMapper.modelToEntity(i))
                        }
                        else{
                            val index = loadedItems.indexOfFirst { it.id == i.id }
                            if (index != -1){
                                loadedItems[index] = TodoNetworkMapper.modelToEntity(i)
                            }
                        }
                    }
                    val updateResponse = todoService.updateList(sharedPrefRepository.getRevision(), TodoItemListRequest(loadedItems))
                    if (updateResponse.isSuccessful){
                        val updateBody = updateResponse.body()
                        if (updateBody != null){
                            todoDao.rewriteTable(
                                updateBody.list.map { TodoMapper.modelToEntity(TodoNetworkMapper.entityToModel(it)) }
                            )
                            sharedPrefRepository.writeRevision(updateBody.revision)
                        }
                    }
                }
            }
        }
        catch (e :Exception){
            Log.e("Repository", e.toString())
        }
    }
}