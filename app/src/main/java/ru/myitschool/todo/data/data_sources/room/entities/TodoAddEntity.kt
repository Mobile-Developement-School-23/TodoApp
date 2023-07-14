package ru.myitschool.todo.data.data_sources.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "todo_add")
data class TodoAddEntity(
    @PrimaryKey val id:String
)
