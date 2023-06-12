package ru.myitschool.todo.repository

import android.icu.text.SimpleDateFormat
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem
import java.security.MessageDigest
import java.util.*

class TodoItemsRepository {
    companion object{
        val todoItems = mutableListOf<TodoItem>()
    }
    constructor(){
        if (todoItems.isEmpty()) {
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
            addItem(TodoItem("1", "Сделать курсовую!", Priority.HIGH, false, Date()))
        }
    }
    fun addItem(todoItem: TodoItem){
        val id = hashString(Date().time.toString())
        todoItem.id = id
        todoItems.add(todoItem)
    }
    fun getItemById(id:String):TodoItem?{
        return todoItems.find { it.id == id }
    }
    fun updateItem(todoItem: TodoItem){
        val index = todoItems.withIndex().find { it.value.id == todoItem.id }?.index
        if (index != null) {
            todoItems[index] = todoItem
        }
    }
    fun deleteItem(id:String){
        val index = todoItems.withIndex().find { it.value.id == id }?.index
        if (index != null) {
            todoItems.removeAt(index)
        }
    }
    private fun hashString(str:String):String{
        return MessageDigest.getInstance("sha-256").digest(str.toByteArray()).fold("", { str, it -> str + "%02x".format(it) })
    }

}