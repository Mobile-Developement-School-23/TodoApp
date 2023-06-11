package ru.myitschool.todo.ui.adapters

import android.icu.text.SimpleDateFormat
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import ru.myitschool.todo.R
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem
import ru.myitschool.todo.repository.TodoItemsRepository
import java.util.*


class TodoListAdapter : RecyclerView.Adapter<TodoListAdapter.TodoListHolder>() {
    var todoList = mutableListOf<TodoItem>()
        set(value) {
            var counter = 0
            for (i in value) {
                if (i.isCompleted) {
                    counter++
                }
            }
            checkedCounter.value = counter
            field = value
        }
    private val repository = TodoItemsRepository()
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
                todoPriority.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_critical, itemView.context.theme))
                todoPriority.visibility = View.VISIBLE
            }
            else if(todoItem.priority == Priority.LOW){
                todoPriority.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_arrow_down, itemView.context.theme))
                todoPriority.visibility = View.VISIBLE
            }
            todoText.text = todoItem.text
            todoCheckBox.isChecked = todoItem.isCompleted
            todoCheckBox.setOnCheckedChangeListener { button, isChecked ->
                run {
                    if (isChecked) {
                        checkedCounter.value = checkedCounter.value?.plus(1)

                    } else {
                        checkedCounter.value = checkedCounter.value?.minus(1)
                    }
                    todoItem.isCompleted = isChecked
                    repository.updateItem(todoItem)
                }
            }
            if (todoItem.deadline != null) {
                todoDeadline.visibility = View.VISIBLE
                val dateFormat = SimpleDateFormat("MMMM d", Locale.getDefault())
                todoDeadline.text = dateFormat.format(todoItem.deadline)
            }
        }
    }
}