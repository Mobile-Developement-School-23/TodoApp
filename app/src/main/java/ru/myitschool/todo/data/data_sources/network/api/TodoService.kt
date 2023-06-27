package ru.myitschool.todo.data.data_sources.network.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.myitschool.todo.data.data_sources.network.entities.TodoItemListResponse
import ru.myitschool.todo.data.data_sources.network.entities.TodoItemRequest
import ru.myitschool.todo.data.data_sources.network.entities.TodoItemResponse

interface TodoService {
    @GET("list")
    suspend fun loadList(@Header("Authorization") auth: String): Response<TodoItemListResponse>

    @GET("list/{id}")
    suspend fun loadTodoItem(
        @Header("Authorization") auth: String,
        @Path("id") id: String
    ): Response<TodoItemResponse>

    @DELETE("list/{id}")
    suspend fun deleteTodoItem(
        @Header("Authorization") auth: String,
        @Header("X-Last-Known-Revision") revision:Int,
        @Path("id") id: String
    ): Response<TodoItemResponse>

    @POST("list")
    suspend fun addTodoItem(@Header("Authorization") auth: String,
                            @Header("X-Last-Known-Revision") revision:Int,
                            @Body todoItem:TodoItemRequest): Response<TodoItemResponse>

    @PUT("list/{id}")
    suspend fun changeTodoItem(@Header("Authorization") auth: String,
                               @Header("X-Last-Known-Revision") revision:Int,
                               @Path("id")id:String,
                               @Body todoItem:TodoItemRequest):Response<TodoItemResponse>
}