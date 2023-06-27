package ru.myitschool.todo.data.data_sources.network.entities

data class TodoItemResponse(
    val status:String,
    val element:TodoItemNetworkEntity,
    val revision: Int
)

