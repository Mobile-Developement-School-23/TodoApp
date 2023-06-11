package ru.myitschool.todo.data.models

import java.util.*

data class TodoItem(
    var id:String,
    var text:String,
    var priority:Int,
    var isCompleted:Boolean,
    var creationDate:Date,
    var deadline: Date? = null,
    var changingDate:Date? = null
    )
class Priority{
    companion object{
        const val LOW = 0
        const val NORMAL = 1
        const val HIGH = 2
    }
}