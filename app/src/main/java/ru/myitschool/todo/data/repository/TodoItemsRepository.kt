package ru.myitschool.todo.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.myitschool.todo.data.data_sources.network.todoitems_server.TodoService
import ru.myitschool.todo.data.data_sources.network.todoitems_server.data_mappers.TodoNetworkMapper
import ru.myitschool.todo.data.data_sources.network.todoitems_server.entities.TodoItemListRequest
import ru.myitschool.todo.data.data_sources.network.todoitems_server.entities.TodoItemRequest
import ru.myitschool.todo.data.data_sources.room.dao.TodoAddDao
import ru.myitschool.todo.data.data_sources.room.dao.TodoDao
import ru.myitschool.todo.data.data_sources.room.dao.TodoDeleteDao
import ru.myitschool.todo.data.data_sources.room.data_mappers.ImportanceMapper
import ru.myitschool.todo.data.data_sources.room.data_mappers.TodoMapper
import ru.myitschool.todo.data.data_sources.room.database.AppDatabase
import ru.myitschool.todo.data.data_sources.room.entities.TodoAddEntity
import ru.myitschool.todo.data.data_sources.room.entities.TodoDeleteEntity
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem
import ru.myitschool.todo.di.AppScope
import ru.myitschool.todo.utils.NetworkStateMonitor
import java.lang.NullPointerException
import java.util.Date
import javax.inject.Inject

@AppScope
class TodoItemsRepository @Inject constructor(
    private val todoService: TodoService,
    private val database: AppDatabase,
    private val networkStateMonitor: NetworkStateMonitor
) {
    private val todoDao: TodoDao by lazy {
        database.todoDao()
    }
    private val addDao: TodoAddDao by lazy {
        database.addDao()
    }
    private val deleteDao: TodoDeleteDao by lazy {
        database.deleteDao()
    }
    val todoItems: Flow<List<TodoItem>> =
        todoDao.loadAllTodoItems().map { list -> list.map { TodoMapper.entityToModel(it) } }

    private val scope = CoroutineScope(Dispatchers.Default)
    private var isConnected = false

    init {
        scope.launch {
            networkStateMonitor.isConnected.collect {
                isConnected = it
            }
        }
    }


    suspend fun addItem(todoItem: TodoItem): Result<Boolean> =
        withContext(Dispatchers.IO) {
            val newTodoItem = todoItem.copy(changingDate = Date())
            todoDao.insertTodoItem(TodoMapper.modelToEntity(newTodoItem))
            val newItem = TodoNetworkMapper.modelToEntity(newTodoItem)
            val requestItem = TodoItemRequest(element = newItem)
            if (isConnected) {
                return@withContext runCatching<Boolean> {
                    val response =
                        todoService.addTodoItem(requestItem)
                    if (!response.isSuccessful) {
                        updateItems()
                        return@runCatching true
                    }
                    return@runCatching false
                }
            }
            addDao.addTodo(TodoAddEntity(todoItem.id))
            return@withContext Result.failure(Exception())
        }

    suspend fun getItemById(id: String): Result<TodoItem?> {
        val todoItemEntity = todoDao.loadTodoItemById(id)
        return if (todoItemEntity == null) {
            Result.failure(Exception())
        } else {
            Result.success(TodoMapper.entityToModel(todoItemEntity))
        }
    }

    suspend fun updateItem(todoItem: TodoItem, withUpdate: Boolean): Result<Boolean> =
        withContext(Dispatchers.IO) {
            var newTodoItem = todoItem
            if (withUpdate) {
                newTodoItem = todoItem.copy(changingDate = Date())
            }
            todoDao.updateTodoItem(TodoMapper.modelToEntity(newTodoItem))
            val newItem = TodoNetworkMapper.modelToEntity(newTodoItem)
            val requestItem = TodoItemRequest(element = newItem)
            if (isConnected) {
                return@withContext runCatching {
                    val response = todoService.changeTodoItem(
                        newItem.id,
                        requestItem
                    )
                    if (!response.isSuccessful) {
                        updateItems()
                        return@runCatching false
                    }
                    return@runCatching true
                }
            }
            return@withContext Result.failure(Exception())
        }

    suspend fun getItemsByPriority(priority: Priority): Result<List<TodoItem>> =
        withContext(Dispatchers.IO) {
            Result.success(todoDao.loadTodoItemsByImportance(
                ImportanceMapper.priorityToImportance(
                    priority
                )
            )
                .map { TodoMapper.entityToModel(it) })
        }

    suspend fun deleteItem(id: String): Result<Boolean> = withContext(Dispatchers.IO) {
        todoDao.deleteById(id)
        addDao.deleteById(id)
        if (isConnected) {
            return@withContext runCatching {
                val response =
                    todoService.deleteTodoItem(id)
                if (!response.isSuccessful) {
                    updateItems()
                    return@runCatching false
                }
                return@runCatching true
            }
        }
        deleteDao.addDeleteItem(TodoDeleteEntity(id, Date().time))
        Result.failure(Exception())
    }

    suspend fun loadAllItems(): Result<Boolean> = withContext(Dispatchers.IO) {
        if (isConnected) {
            return@withContext runCatching {
                val response = todoService.loadList()
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        todoDao.rewriteTable(result.list.map {
                            TodoMapper.modelToEntity(
                                TodoNetworkMapper.entityToModel(it)
                            )
                        })
                        return@runCatching true
                    }
                    throw NullPointerException("Result is null")
                }
                throw Exception(response.errorBody()?.string())
            }
        }
        return@withContext Result.failure(Exception("Connection error"))
    }

    suspend fun getAllItems(): Result<List<TodoItem>> =
        Result.success(todoDao.loadAllTodoItemsAsync().map { TodoMapper.entityToModel(it) })

    suspend fun updateItems(): Result<Boolean> = withContext(Dispatchers.IO) {
        if (isConnected) {
            return@withContext runCatching<Boolean> {
                val response = todoService.loadList()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        var savedItems: List<TodoItem> = listOf()
                        getAllItems().onSuccess {
                            savedItems = it
                        }.onFailure {
                            throw it
                        }
                        val loadedItems = body.list.toMutableList()
                        val isSimilar =
                            loadedItems.map { TodoNetworkMapper.entityToModel(it) }
                                .toSet() == savedItems.toSet()
                        if (isSimilar) {
                            return@runCatching true
                        }
                        val deletedItems = deleteDao.getAll()
                        val addedItems = addDao.getAll()
                        for (i in savedItems) { //Тут должна быть рабочая синхронизация, но удаление чет пошло по ж***
                            val index = loadedItems.indexOfFirst { it.id == i.id }
                            if (index != -1) {
                                loadedItems[index] = TodoNetworkMapper.modelToEntity(i)
                            }
                            else{
                                if (addedItems.any { it.id == i.id}){
                                    loadedItems.add(TodoNetworkMapper.modelToEntity(i))
                                }
                            }
                        }
                        for (i in deletedItems) {
                            val index = loadedItems.indexOfFirst { it.id == i.id }
                            if (index != -1) {
                                loadedItems.removeAt(index)
                            }
                        }
                        val updateResponse = todoService.updateList(
                            TodoItemListRequest(loadedItems)
                        )
                        if (updateResponse.isSuccessful) {
                            val updateBody = updateResponse.body()
                            if (updateBody != null) {
                                todoDao.rewriteTable(
                                    updateBody.list.map {
                                        TodoMapper.modelToEntity(
                                            TodoNetworkMapper.entityToModel(
                                                it
                                            )
                                        )
                                    }
                                )
                            }
                            deleteDao.deleteAll()
                            addDao.deleteAll()
                            return@runCatching true
                        }
                        throw Exception("Connection error")
                    }
                }
                throw Exception("Connection error")
            }
        }
        return@withContext Result.failure(Exception("Connection error"))
    }
}