package ru.myitschool.todo

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import ru.myitschool.todo.databinding.ActivityMainBinding
import ru.myitschool.todo.databinding.FragmentTodoListBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null

    private val binding get() = _binding!!
    private val sharedPreferences:SharedPreferences by lazy {
        getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        when(sharedPreferences.getInt("theme", 2)){
            0->{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            1->{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            2->{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
        setContentView(binding.root)
    }
}