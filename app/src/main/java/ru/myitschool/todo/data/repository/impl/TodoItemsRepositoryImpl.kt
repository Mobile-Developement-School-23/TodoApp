package ru.myitschool.todo.data.repository.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.myitschool.todo.data.data_sources.network.todoitems_server.TodoService
import ru.myitschool.todo.data.data_sources.network.todoitems_server.data_mappers.TodoNetworkMapper
import ru.myitschool.todo.data.data_sources.network.todoitems_server.entities.TodoItemListRequest
import ru.myitschool.todo.data.data_sources.network.todoitems_server.entities.TodoItemNetworkEntity
import ru.myitschool.todo.data.data_sources.network.todoitems_server.entities.TodoItemRequest
import ru.myitschool.todo.data.data_sources.room.dao.TodoAddDao
import ru.myitschool.todo.data.data_sources.room.dao.TodoDao
import ru.myitschool.todo.data.data_sources.room.dao.TodoDeleteDao
import ru.myitschool.todo.data.data_sources.room.data_mappers.ImportanceMapper
import ru.myitschool.todo.data.data_sources.room.data_mappers.TodoMapper
import ru.myitschool.todo.data.data_sources.room.database.AppDatabase
import ru.myitschool.todo.data.data_sources.room.entities.TodoAddEntity
import ru.myitschool.todo.data.data_sources.room.entities.TodoDeleteEntity
import ru.myitschool.todo.data.data_sources.room.entities.TodoItemEntity
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem
import ru.myitschool.todo.data.repository.TodoItemsRepository
import ru.myitschool.todo.di.scopes.AppScope
import ru.myitschool.todo.utils.NetworkStateMonitor
import ru.myitschool.todo.utils.exceptions.BadRequestException
import ru.myitschool.todo.utils.exceptions.NotFoundException
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.Date
import javax.inject.Inject

@AppScope
class TodoItemsRepositoryImpl @Inject constructor(
    private val todoService: TodoService,
    private val addDao: TodoAddDao,
    private val todoDao: TodoDao,
    private val deleteDao: TodoDeleteDao,
    private val networkStateMonitor: NetworkStateMonitor
) : TodoItemsRepository {
    private val todoItems: Flow<List<TodoItem>> =
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

    private val connectException = ConnectException("Connection error")

    override fun getItemsFlow(): Flow<List<TodoItem>> = todoItems
    override suspend fun addItem(todoItem: TodoItem): Result<Boolean> =
        withContext(Dispatchers.IO) {
            val newTodoItem = todoItem.copy(changingDate = Date())
            todoDao.insertTodoItem(TodoMapper.modelToEntity(newTodoItem))
            val newItem = TodoNetworkMapper.modelToEntity(newTodoItem)
            val requestItem = TodoItemRequest(element = newItem)
            if (isConnected) {
                try {
                    todoService.addTodoItem(requestItem)
                    return@withContext Result.success(true)
                } catch (e: UnknownHostException) {
                    return@withContext Result.failure(e)
                } catch (e: retrofit2.HttpException) {
                    updateItems().onSuccess {
                        return@withContext Result.success(it)
                    }.onFailure {
                        return@withContext Result.failure(it)
                    }
                }catch (e:Exception){
                    return@withContext Result.failure(e)
                }
            }
            addDao.addTodo(TodoAddEntity(todoItem.id))
            return@withContext Result.failure(connectException)
        }

    override suspend fun getItemById(id: String): Result<TodoItem?> {
        val todoItemEntity = todoDao.loadTodoItemById(id)
        return if (todoItemEntity == null) {
            Result.failure(NotFoundException())
        } else {
            Result.success(TodoMapper.entityToModel(todoItemEntity))
        }
    }

    override suspend fun updateItem(todoItem: TodoItem, withUpdate: Boolean): Result<Boolean> =
        withContext(Dispatchers.IO) {
            var newTodoItem = todoItem
            if (withUpdate) {
                newTodoItem = todoItem.copy(changingDate = Date())
            }
            todoDao.updateTodoItem(TodoMapper.modelToEntity(newTodoItem))
            val newItem = TodoNetworkMapper.modelToEntity(newTodoItem)
            val requestItem = TodoItemRequest(element = newItem)
            if (isConnected) {
                try {
                    todoService.changeTodoItem(newItem.id, requestItem)
                    return@withContext Result.success(true)
                } catch (e: UnknownHostException) {
                    return@withContext Result.failure(e)
                } catch (e: retrofit2.HttpException) {
                    updateItems().onSuccess {
                        return@withContext Result.success(it)
                    }.onFailure {
                        return@withContext Result.failure(it)
                    }
                }catch (e:Exception){
                    return@withContext Result.failure(e)
                }
            }
            return@withContext Result.failure(connectException)
        }

    override suspend fun getItemsByPriority(priority: Priority): Result<List<TodoItem>> =
        withContext(Dispatchers.IO) {
            Result.success(todoDao.loadTodoItemsByImportance(
                ImportanceMapper.priorityToImportance(
                    priority
                )
            )
                .map { TodoMapper.entityToModel(it) })
        }

    override suspend fun deleteItem(id: String): Result<Boolean> = withContext(Dispatchers.IO) {
        todoDao.deleteById(id)
        addDao.deleteById(id)
        if (isConnected) {
            try {
                todoService.deleteTodoItem(id)
                return@withContext Result.success(true)
            } catch (e: UnknownHostException) {
                return@withContext Result.failure(e)
            } catch (e: retrofit2.HttpException) {
                updateItems().onSuccess {
                    return@withContext Result.success(it)
                }.onFailure {
                    return@withContext Result.failure(it)
                }
            }catch (e:Exception){
                return@withContext Result.failure(e)
            }
        }
        deleteDao.addDeleteItem(TodoDeleteEntity(id, Date().time))
        Result.failure(connectException)
    }

    override suspend fun loadAllItems(): Result<Boolean> = withContext(Dispatchers.IO) {
        if (isConnected) {
            return@withContext runCatching<Boolean> {
                val response = todoService.loadList()
                todoDao.rewriteTable(response.list.map {
                    TodoMapper.modelToEntity(TodoNetworkMapper.entityToModel(it))
                })
                return@runCatching true
            }
        }
        return@withContext Result.failure(connectException)
    }

    override suspend fun getAllItems(): Result<List<TodoItem>> =
        Result.success(todoDao.loadAllTodoItemsAsync().map { TodoMapper.entityToModel(it) })

    override suspend fun updateItems(): Result<Boolean> = withContext(Dispatchers.IO) {
        if (isConnected) {
            return@withContext runCatching {
                val response = todoService.loadList()
                var savedItems: List<TodoItem> = listOf()
                getAllItems().onSuccess {
                    savedItems = it
                }.onFailure {
                    throw it
                }
                val loadedItems = response.list.toMutableList()
                val isSimilar =
                    loadedItems.map { TodoNetworkMapper.entityToModel(it) }
                        .toSet() == savedItems.toSet()
                if (isSimilar) {
                    return@runCatching true
                }
                val syncedItems = sync(savedItems, loadedItems, addDao.getAll(), deleteDao.getAll())
                val updateResponse = todoService.updateList(
                    TodoItemListRequest(syncedItems)
                )
                todoDao.rewriteTable(
                    updateResponse.list.map {
                        TodoMapper.modelToEntity(TodoNetworkMapper.entityToModel(it))
                    }
                )
                return@runCatching true
            }
        }
        return@withContext Result.failure(connectException)
    }
    private fun sync(savedItems:List<TodoItem>,
                     loadedItems:MutableList<TodoItemNetworkEntity>,
                     addedItems:List<TodoAddEntity>,
                     deletedItems:List<TodoDeleteEntity>):List<TodoItemNetworkEntity>{
        for (i in savedItems) { //Тут должна быть рабочая синхронизация, но удаление чет пошло по ж***
            val index = loadedItems.indexOfFirst { it.id == i.id }
            if (index != -1) {
                loadedItems[index] = TodoNetworkMapper.modelToEntity(i)
            } else {
                if (addedItems.any { it.id == i.id }) {
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
        return loadedItems
    }
}