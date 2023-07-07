package ru.myitschool.todo.data.data_sources.network.todoitems_server.entities

data class TodoItemListResponse(
    val status:String,
    val list:List<TodoItemNetworkEntity>,
    val revision: Int
)
