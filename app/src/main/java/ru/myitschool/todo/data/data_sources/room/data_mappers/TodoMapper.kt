package ru.myitschool.todo.data.data_sources.room.data_mappers

import ru.myitschool.todo.data.data_sources.room.entities.TodoItemEntity
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem
import java.util.Date

object TodoMapper {
    fun modelToEntity(todoItem: TodoItem):TodoItemEntity {
        return TodoItemEntity(
            id = todoItem.id,
            text = todoItem.text,
            importance = ImportanceMapper.priorityToImportance(todoItem.priority),
            isDone = todoItem.isCompleted,
            createdAt = todoItem.creationDate.time,
            deadline = todoItem.deadline?.time,
            changedAt = todoItem.changingDate?.time
            )
    }
    fun entityToModel(todoItemEntity: TodoItemEntity)=
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