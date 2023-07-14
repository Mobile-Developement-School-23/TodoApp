package ru.myitschool.todo.utils

import android.content.Context
import ru.myitschool.todo.R
import ru.myitschool.todo.data.models.Priority

fun getStringPriority(context:Context, priority: Priority):String{
    return when(priority){
        Priority.HIGH->{
            context.getString(R.string.high)
        }
        Priority.LOW->{
            context.getString(R.string.low)
        }
        Priority.NORMAL->{
            context.getString(R.string.no)
        }
    }
}