package ru.myitschool.todo.ui.TodoListFragment.view.recycler

interface ItemTouchHelperAdapter {
    fun onItemDismiss(position:Int)
    fun onItemChecked(position: Int)
}