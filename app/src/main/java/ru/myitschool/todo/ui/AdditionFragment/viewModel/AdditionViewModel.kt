package ru.myitschool.todo.ui.AdditionFragment.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.myitschool.todo.data.models.Priority
import java.util.*

class AdditionViewModel:ViewModel() {
    val priority = MutableLiveData(Priority.NORMAL)
    val text = MutableLiveData("")
    val deadlineDate = MutableLiveData<Date?>()
    fun setPriority(value:Int){
        priority.value = value
    }
    fun setText(value:String){
        text.value = value
    }
    fun setDeadline(value:Date){
        deadlineDate.value = value
    }
}