package ru.myitschool.todo.ui.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.myitschool.todo.R
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem
import ru.myitschool.todo.domain.CommonCallbackImpl
import ru.myitschool.todo.repository.TodoItemsRepository
import ru.myitschool.todo.ui.TodoListFragment.viewModel.TodoListViewModel
import ru.myitschool.todo.ui.TodoListFragment.view.recycler.ItemTouchHelperAdapter
import java.util.*


class TodoListAdapter(
    private val viewModel:TodoListViewModel
) : RecyclerView.Adapter<TodoListAdapter.TodoListHolder>(), ItemTouchHelperAdapter {
    var todoList = mutableListOf<TodoItem>()
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
                areItemsTheSameImpl = {oldItem, newItem ->  oldItem.id == newItem.id},
                areContentsTeSameImpl = {oldItem, newItem ->  oldItem.deadline == newItem.deadline && oldItem.text == newItem.text &&
                        oldItem.priority == newItem.priority}
            )
            checkedCounter.value = counter
            field = value
            val diffResult = DiffUtil.calculateDiff(callback)
            diffResult.dispatchUpdatesTo(this)
        }
    var checkedCounter = MutableLiveData(0)
    val selectedTodoItem = MutableLiveData<TodoItem>()
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

    inner class TodoListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var todoItem:TodoItem
        private val todoText: TextView = itemView.findViewById(R.id.todo_text)
        private val todoCheckBox: CheckBox = itemView.findViewById(R.id.todo_checkbox)
        private val todoDeadline: TextView = itemView.findViewById(R.id.deadline_textview)
        private val todoPriority: ImageView = itemView.findViewById(R.id.high_priority_image)
        fun onBind(todoItem: TodoItem) {
            this.todoItem = todoItem
            itemView.setOnClickListener {
                selectedTodoItem.value = todoItem
            }
            if (todoItem.priority == Priority.HIGH) {
                todoCheckBox.buttonTintList =
                    itemView.resources.getColorStateList(R.color.red, itemView.context.theme)
//                todoCheckBox.setTextAppearance(R.style.CheckBox_HighPriority)
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
            }
            else{
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
            }
            else{
                todoDeadline.visibility = View.GONE
            }
        }
        private fun onChangeChecked(isChecked:Boolean){
            val typedValue = TypedValue()
            val colorId:Int
            if (isChecked) {
                colorId = R.attr.inactiveColor
                todoText.paintFlags = todoText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                checkedCounter.value = checkedCounter.value?.plus(1)
                todoCheckBox.buttonTintList = itemView.resources.getColorStateList(R.color.green, itemView.context.theme)

            } else {
                colorId = androidx.constraintlayout.widget.R.attr.textFillColor
                todoText.paintFlags =
                    todoText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                checkedCounter.value = checkedCounter.value?.minus(1)
                if (todoItem.priority == Priority.HIGH){
                    todoCheckBox.buttonTintList = itemView.resources.getColorStateList(R.color.red, itemView.context.theme)
                }
                else{
                    val colorValue = TypedValue()
                    itemView.context.theme.resolveAttribute(
                        R.attr.inactiveColor,
                        colorValue,
                        true
                    )
                    todoCheckBox.buttonTintList = itemView.resources.getColorStateList(colorValue.resourceId, itemView.context.theme)
                }
            }
            itemView.context.theme.resolveAttribute(
                colorId,
                typedValue,
                true
            )
            todoText.setTextColor(typedValue.data)
            todoItem.isCompleted = isChecked
            viewModel.updateItem(todoItem)
        }
    }

    override fun onItemDismiss(position: Int) {
        viewModel.deleteItem(todoList[position].id)
        notifyItemRemoved(position)
    }

    override fun onItemChecked(position: Int) {
        todoList[position].isCompleted = !todoList[position].isCompleted
        viewModel.updateItem(todoList[position])
        notifyItemChanged(position)
    }
}