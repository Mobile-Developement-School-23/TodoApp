package ru.myitschool.todo.data.data_sources.network.todoitems_server

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.myitschool.todo.data.data_sources.network.todoitems_server.entities.TodoItemListRequest
import ru.myitschool.todo.data.data_sources.network.todoitems_server.entities.TodoItemListResponse
import ru.myitschool.todo.data.data_sources.network.todoitems_server.entities.TodoItemRequest
import ru.myitschool.todo.data.data_sources.network.todoitems_server.entities.TodoItemResponse

interface TodoService {
    @GET("list")
    suspend fun loadList(): Response<TodoItemListResponse>

    @GET("list/{id}")
    suspend fun loadTodoItem(
        @Path("id") id: String
    ): Response<TodoItemResponse>

    @DELETE("list/{id}")
    suspend fun deleteTodoItem(
        @Path("id") id: String
    ): Response<TodoItemResponse>

    @POST("list")
    suspend fun addTodoItem(
                            @Body todoItem: TodoItemRequest
    ): Response<TodoItemResponse>

    @PUT("list/{id}")
    suspend fun changeTodoItem(
                               @Path("id")id:String,
                               @Body todoItem: TodoItemRequest
    ):Response<TodoItemResponse>
    @PATCH("list")
    suspend fun updateList(
        @Body itemsList: TodoItemListRequest
    ):Response<TodoItemListResponse>
}