package ru.myitschool.todo.data.data_sources.network.todoitems_server.data_mappers

import ru.myitschool.todo.data.data_sources.network.todoitems_server.entities.TodoItemNetworkEntity
import ru.myitschool.todo.data.data_sources.room.data_mappers.ImportanceMapper
import ru.myitschool.todo.data.data_sources.room.entities.TodoItemEntity
import ru.myitschool.todo.data.models.TodoItem
import java.util.Date

object TodoNetworkMapper {
    fun modelToEntity(todoItem: TodoItem): TodoItemNetworkEntity {
        return TodoItemNetworkEntity(
            id = todoItem.id,
            text = todoItem.text,
            importance = ImportanceMapper.priorityToImportance(todoItem.priority),
            isDone = todoItem.isCompleted,
            createdAt = todoItem.creationDate.time,
            deadline = todoItem.deadline?.time,
            changedAt = todoItem.changingDate?.time ?: todoItem.creationDate.time
        )
    }
    fun entityToModel(todoItemEntity: TodoItemNetworkEntity)=
        TodoItem(
            id = todoItemEntity.id,
            text = todoItemEntity.text,
            priority = ImportanceMapper.importanceToPriority(todoItemEntity.importance),
            isCompleted = todoItemEntity.isDone,
            creationDate = Date(todoItemEntity.createdAt),
            deadline = if (todoItemEntity.deadline != null) Date(todoItemEntity.deadline) else null,
            changingDate = if (todoItemEntity.changedAt != null) Date(todoItemEntity.changedAt) else null
        )
}