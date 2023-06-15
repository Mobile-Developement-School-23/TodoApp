package ru.myitschool.todo.ui.adapters

import android.content.Context
import android.graphics.Paint
import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
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
import ru.myitschool.todo.ui.recycler.ItemTouchHelperAdapter
import java.util.*


class TodoListAdapter(
    private val viewModel:TodoListViewModel
) : RecyclerView.Adapter<TodoListAdapter.TodoListHolder>(),ItemTouchHelperAdapter{
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
        private val todoText: TextView = itemView.findViewById(R.id.todo_text)
        private val todoCheckBox: CheckBox = itemView.findViewById(R.id.todo_checkbox)
        private val todoDeadline: TextView = itemView.findViewById(R.id.deadline_textview)
        private val todoPriority: ImageView = itemView.findViewById(R.id.high_priority_image)
        fun onBind(todoItem: TodoItem) {
            itemView.setOnClickListener {
                selectedTodoItem.value = todoItem
            }
            if (todoItem.priority == Priority.HIGH) {
                todoCheckBox.buttonTintList =
                    itemView.resources.getColorStateList(R.color.red, itemView.context.theme)
                todoCheckBox.setTextAppearance(R.style.CheckBox_HighPriority)
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
            todoText.text = todoItem.text
            todoCheckBox.setOnCheckedChangeListener { button, isChecked ->
                run {
                    val typedValue = TypedValue()
                    if (isChecked) {
                        itemView.context.theme.resolveAttribute(
                            R.attr.inactiveColor,
                            typedValue,
                            true
                        )
                        todoText.setTextColor(typedValue.data)
                        todoText.paintFlags = todoText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        checkedCounter.value = checkedCounter.value?.plus(1)

                    } else {
                        itemView.context.theme.resolveAttribute(
                            androidx.constraintlayout.widget.R.attr.textFillColor,
                            typedValue,
                            true
                        )
                        todoText.setTextColor(typedValue.data)
                        todoText.paintFlags =
                            todoText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                        println(Paint.STRIKE_THRU_TEXT_FLAG)
                        println(Paint.STRIKE_THRU_TEXT_FLAG.inv())
                        checkedCounter.value = checkedCounter.value?.minus(1)
                    }
                    todoItem.isCompleted = isChecked
                    viewModel.updateItem(todoItem)
                }
            }
            todoCheckBox.isChecked = todoItem.isCompleted
            if (todoItem.deadline != null) {
                todoDeadline.visibility = View.VISIBLE
                val dateFormat = SimpleDateFormat("MMMM d", Locale.getDefault())
                todoDeadline.text = dateFormat.format(todoItem.deadline)
            }
        }
    }

    override fun onItemDismiss(position: Int) {
        todoList.removeAt(position)
        notifyItemRemoved(position)
        viewModel.
    }
}