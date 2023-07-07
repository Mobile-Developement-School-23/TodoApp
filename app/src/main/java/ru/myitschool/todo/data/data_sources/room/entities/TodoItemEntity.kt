package ru.myitschool.todo.data.data_sources.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_items")
data class TodoItemEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "importance") val importance: String,
    @ColumnInfo(name = "done") val isDone: Boolean,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "deadline") val deadline: Long?,
    @ColumnInfo(name = "changed_at") val changedAt: Long?
)