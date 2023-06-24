package ru.myitschool.todo.data.models

import java.util.*

data class TodoItem(
    val id:String,
    val text:String,
    val priority:Priority,
    val isCompleted:Boolean,
    val creationDate:Date,
    val deadline: Date? = null,
    val changingDate:Date? = null
    )
enum class Priority{
    LOW,
    NORMAL,
    HIGH
}