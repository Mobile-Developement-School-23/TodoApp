package ru.myitschool.todo.ui.adapters

import android.graphics.Paint
import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.myitschool.todo.R
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem
import ru.myitschool.todo.domain.CommonCallbackImpl
import ru.myitschool.todo.ui.TodoListFragment.view.recycler.ItemTouchHelperAdapter
import java.util.*


class TodoListAdapter(
    private val itemChanger: ItemChanger,
    private val selectedCallback: SelectedCallback,
    private val counterCallback: CounterCallback
) : RecyclerView.Adapter<TodoListAdapter.TodoListHolder>(), ItemTouchHelperAdapter {
    var todoList = listOf<TodoItem>()
        set(value) {
            var counter = 0
            for (i in value) {
                if (i.isCompleted) {
                    counter++
                }
            }
            val callback = CommonCallbackImpl(
                oldItems = field,
                newItems = value,
                areItemsTheSameImpl = { oldItem, newItem -> oldItem.id == newItem.id },
                areContentsTeSameImpl = { oldItem, newItem ->
                    oldItem.deadline == newItem.deadline && oldItem.text == newItem.text &&
                            oldItem.priority == newItem.priority && oldItem.isCompleted == newItem.isCompleted
                }
            )
            counterCallback.onCount(counter)
            field = value.toMutableList()
            val diffResult = DiffUtil.calculateDiff(callback)
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TodoListHolder(
            layoutInflater.inflate(
                R.layout.todo_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TodoListHolder, position: Int) {
        holder.onBind(todoList[position])
    }

    override fun getItemCount(): Int = todoList.size
    override fun onItemDismiss(position: Int) {
        itemChanger.deleteItem(todoList[position].id)
    }

    override fun onItemChecked(position: Int) {
        val todoItem = todoList[position].copy(isCompleted = !todoList[position].isCompleted)
        itemChanger.updateItem(todoItem, false)
    }

    inner class TodoListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var todoItem: TodoItem
        private val todoText: TextView = itemView.findViewById(R.id.todo_text)
        private val todoCheckBox: CheckBox = itemView.findViewById(R.id.todo_checkbox)
        private val todoDeadline: TextView = itemView.findViewById(R.id.deadline_textview)
        private val todoPriority: ImageView = itemView.findViewById(R.id.high_priority_image)
        fun onBind(todoItem: TodoItem) {
            this.todoItem = todoItem
            itemView.setOnClickListener {
                selectedCallback.onSelect(todoItem)
            }
            if (todoItem.priority == Priority.HIGH) {
                todoCheckBox.buttonTintList =
                    itemView.resources.getColorStateList(R.color.red, itemView.context.theme)
                todoPriority.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        itemView.resources,
                        R.drawable.ic_critical,
                        itemView.context.theme
                    )
                )
                todoPriority.visibility = View.VISIBLE
            } else if (todoItem.priority == Priority.LOW) {
                todoPriority.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        itemView.resources,
                        R.drawable.ic_arrow_down,
                        itemView.context.theme
                    )
                )
                todoPriority.visibility = View.VISIBLE
            } else {
                todoPriority.visibility = View.INVISIBLE
            }
            todoText.text = todoItem.text
            todoCheckBox.setOnCheckedChangeListener { button, isChecked ->
                onChangeChecked(isChecked)
            }
            todoCheckBox.isChecked = todoItem.isCompleted
            onChangeChecked(todoItem.isCompleted)
            if (todoItem.deadline != null) {
                todoDeadline.visibility = View.VISIBLE
                val dateFormat = SimpleDateFormat("MMMM d", Locale.getDefault())
                todoDeadline.text = dateFormat.format(todoItem.deadline)
            } else {
                todoDeadline.visibility = View.GONE
            }
        }

        private fun onChangeChecked(isChecked: Boolean) {
            val typedValue = TypedValue()
            val colorId: Int
            if (isChecked) {
                colorId = R.attr.inactiveColor
                todoText.paintFlags = todoText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                todoCheckBox.buttonTintList =
                    itemView.resources.getColorStateList(R.color.green, itemView.context.theme)

            } else {
                colorId = androidx.constraintlayout.widget.R.attr.textFillColor
                todoText.paintFlags =
                    todoText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                if (todoItem.priority == Priority.HIGH) {
                    todoCheckBox.buttonTintList =
                        itemView.resources.getColorStateList(R.color.red, itemView.context.theme)
                } else {
                    val colorValue = TypedValue()
                    itemView.context.theme.resolveAttribute(
                        R.attr.inactiveColor,
                        colorValue,
                        true
                    )
                    todoCheckBox.buttonTintList = itemView.resources.getColorStateList(
                        colorValue.resourceId,
                        itemView.context.theme
                    )
                }
            }
            itemView.context.theme.resolveAttribute(
                colorId,
                typedValue,
                true
            )
            todoText.setTextColor(typedValue.data)
            itemChanger.updateItem(todoItem.copy(isCompleted = isChecked), false)
        }
    }
}

interface ItemChanger {
    fun updateItem(todoItem: TodoItem, toTop: Boolean)
    fun deleteItem(id: String)
}
interface SelectedCallback{
    fun onSelect(todoItem: TodoItem)
}
interface CounterCallback{
    fun onCount(count:Int)
}