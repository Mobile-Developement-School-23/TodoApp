package ru.myitschool.todo.data.repository

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.myitschool.todo.data.data_sources.network.api.TodoService
import ru.myitschool.todo.data.data_sources.network.data_mappers.TodoNetworkMapper
import ru.myitschool.todo.data.data_sources.network.entities.TodoItemRequest
import ru.myitschool.todo.data.data_sources.room.dao.TodoDao
import ru.myitschool.todo.data.data_sources.room.data_mappers.ImportanceMapper
import ru.myitschool.todo.data.data_sources.room.data_mappers.TodoMapper
import ru.myitschool.todo.data.data_sources.room.database.AppDatabase
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem
import java.lang.Exception
import java.security.MessageDigest
import java.util.Date
import java.util.Random

class TodoItemsRepository(val context: Context) {

    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }
    private val todoDao: TodoDao by lazy {
        database.todoDao()
    }
    val todoItems: Flow<List<TodoItem>> =
        todoDao.loadAllTodoItems().map { list -> list.map { TodoMapper.entityToModel(it) } }
    private val BASE_URL = "https://beta.mrdekk.ru/todobackend/"

    private val retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()

    private val todoService: TodoService = retrofit.create(TodoService::class.java)
    private val sharedPrefRepository = SharedPreferencesRepository(
        context.getSharedPreferences(
            "AppSettings",
            Context.MODE_PRIVATE
        )
    )

    private val token = "Bearer astely"


    suspend fun addItem(todoItem: TodoItem) {
        val newTodoItem = todoItem.copy(changingDate = Date())
        todoDao.insertTodoItem(TodoMapper.modelToEntity(newTodoItem))
        val newItem = TodoNetworkMapper.modelToEntity(newTodoItem)
        val requestItem = TodoItemRequest(element = newItem)
        try {
            val response =
                todoService.addTodoItem(token, sharedPrefRepository.getRevision(), requestItem)
            println(response.message())
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null){
                    sharedPrefRepository.writeRevision(result.revision)
                }
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

    suspend fun updateItem(todoItem: TodoItem, withUpdate:Boolean) {
        var newTodoItem = todoItem
        if (withUpdate) {
            newTodoItem = todoItem.copy(changingDate = Date())
        }
        todoDao.updateTodoItem(TodoMapper.modelToEntity(newTodoItem))
        val newItem = TodoNetworkMapper.modelToEntity(newTodoItem)
        val requestItem = TodoItemRequest(element = newItem)
        try {
            val response = todoService.changeTodoItem(
                token,
                sharedPrefRepository.getRevision(),
                newItem.id,
                requestItem
            )
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null){
                    sharedPrefRepository.writeRevision(result.revision)
                }
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
            val response = todoService.deleteTodoItem(token, sharedPrefRepository.getRevision(), id)
            if (response.isSuccessful){
                val result = response.body()
                if (result != null){
                    sharedPrefRepository.writeRevision(result.revision)
                }
            }
        } catch (_:Exception){

        }
    }

    suspend fun loadAllItems(func:(value:Boolean)->Unit) {
        try {
            val response = todoService.loadList(token)
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
                }
                else{
                    func(false)
                }
            }
            else{
                func(false)
            }
        } catch (_: Exception) {
            func(false)
        }
    }
    suspend fun getAllItems():List<TodoItem> = todoDao.loadAllTodoItemsAsync().map { TodoMapper.entityToModel(it) }

    suspend fun rewriteTodoList(todoItems: List<TodoItem>) {
        todoDao.rewriteTable(todoItems.map { TodoMapper.modelToEntity(it) })
    }
}