package ru.myitschool.todo.data.data_sources.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_delete")
data class TodoDeleteEntity(
    @PrimaryKey val id:String,
    @ColumnInfo(name = "deleted_at") val deletedAt:Long
)