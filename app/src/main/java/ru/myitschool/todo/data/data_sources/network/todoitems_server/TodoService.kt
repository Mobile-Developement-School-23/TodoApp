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
    companion object{
        const val BASE_URL = "https://beta.mrdekk.ru/todobackend/"
    }
    @GET("list")
    suspend fun loadList(@Header("Authorization") auth:String,): Response<TodoItemListResponse>

    @GET("list/{id}")
    suspend fun loadTodoItem(@Header("Authorization") auth:String,
        @Path("id") id: String
    ): Response<TodoItemResponse>

    @DELETE("list/{id}")
    suspend fun deleteTodoItem(@Header("Authorization") auth:String,
        @Header("X-Last-Known-Revision") revision:Int,
        @Path("id") id: String
    ): Response<TodoItemResponse>

    @POST("list")
    suspend fun addTodoItem(@Header("Authorization") auth:String,
                            @Header("X-Last-Known-Revision") revision:Int,
                            @Body todoItem: TodoItemRequest
    ): Response<TodoItemResponse>

    @PUT("list/{id}")
    suspend fun changeTodoItem(@Header("Authorization") auth:String,
                               @Header("X-Last-Known-Revision") revision:Int,
                               @Path("id")id:String,
                               @Body todoItem: TodoItemRequest
    ):Response<TodoItemResponse>
    @PATCH("list")
    suspend fun updateList(@Header("Authorization") auth:String,
        @Header("X-Last-Known-Revision") revision: Int,
        @Body itemsList: TodoItemListRequest
    ):Response<TodoItemListResponse>
}