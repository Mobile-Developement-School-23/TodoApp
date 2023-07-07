package ru.myitschool.todo.data.data_sources.network.todoitems_server.entities

data class TodoItemResponse(
    val status:String,
    val element: TodoItemNetworkEntity,
    val revision: Int
)

