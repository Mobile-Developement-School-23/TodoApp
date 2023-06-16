package ru.myitschool.todo.data.models

import java.util.*

data class TodoItem(
    var id:String,
    var text:String,
    var priority:Priority,
    var isCompleted:Boolean,
    var creationDate:Date,
    var deadline: Date? = null,
    var changingDate:Date? = null
    )
enum class Priority{
    LOW,
    NORMAL,
    HIGH
}