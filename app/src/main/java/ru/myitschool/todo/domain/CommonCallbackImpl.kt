package ru.myitschool.todo.domain

import androidx.recyclerview.widget.DiffUtil
import ru.myitschool.todo.data.models.TodoItem

class TodoCallbackImpl<T>(
    private val oldItems: List<T>,
    private val newItems: List<T>,
    private val areItemsTheSameImpl: (oldItem: T, newItem: T) -> Boolean = { oldItem, newItem ->
        oldItem == newItem
    },
    private val areContentsTeSameImpl: (oldItem: T, newItem: T) -> Boolean = { oldItem, newItem ->
        oldItem == newItem
    }
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
        return areItemsTheSameImpl(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
        return areContentsTeSameImpl(oldItem, newItem)
    }

}