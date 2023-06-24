package ru.myitschool.todo.data.data_sources.room.data_mappers

import ru.myitschool.todo.data.models.Priority

object ImportanceMapper {
    fun importanceToPriority(importance:String):Priority{
        var impToPriority: Priority = Priority.NORMAL
        when (importance) {
            "low" -> {
                impToPriority = Priority.LOW
            }

            "important" -> {
                impToPriority = Priority.HIGH
            }
        }
        return impToPriority
    }
    fun priorityToImportance(priority: Priority):String{
        return when(priority){
            Priority.HIGH->{
                "important"
            }

            Priority.LOW->{
                "low"
            }

            Priority.NORMAL->{
                "basic"
            }
        }
    }
}