package ru.myitschool.todo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.myitschool.todo.databinding.ActivityMainBinding
import ru.myitschool.todo.databinding.FragmentTodoListBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}