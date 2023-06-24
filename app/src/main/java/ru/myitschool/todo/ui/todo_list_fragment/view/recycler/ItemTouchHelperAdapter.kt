package ru.myitschool.todo.ui.todo_list_fragment.view.recycler

interface ItemTouchHelperAdapter {
    fun onItemDismiss(position:Int)
    fun onItemChecked(position: Int)
}