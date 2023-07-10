package ru.myitschool.todo.ui.todo_list_fragment.recycler

import ru.myitschool.todo.data.models.TodoItem

interface ItemChanger {
    fun updateItem(todoItem: TodoItem, toTop: Boolean)
    fun deleteItem(id: String)
}
interface SelectedCallback{
    fun onSelect(todoItem: TodoItem)
    fun onSwipeStart()
    fun onSwipeFinish()
}
interface CounterCallback{
    fun onCount(count:Int)
}

interface OnCurrentListChangedListener{
    fun <T>onCurrentListChanged(previous:MutableList<T>, current:MutableList<T>)
}