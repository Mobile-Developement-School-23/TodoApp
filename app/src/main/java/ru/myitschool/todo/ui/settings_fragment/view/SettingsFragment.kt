package ru.myitschool.todo.ui.settings_fragment.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import ru.myitschool.todo.R
import ru.myitschool.todo.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    private var _binding:FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val navController: NavController by lazy {
        NavHostFragment.findNavController(this)
    }
    private val sharedPreferences:SharedPreferences by lazy{
        requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when(sharedPreferences.getInt("theme", 2)){
            0->{
                binding.themeSelector.check(R.id.light_theme_button)
            }
            1->{
                binding.themeSelector.check(R.id.dark_theme_button)
            }
            2-> {
                binding.themeSelector.check(R.id.system_theme_button)
            }
        }
        binding.themeSelector.jumpDrawablesToCurrentState()
        binding.themeSelector.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId){
                R.id.light_theme_button->{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    sharedPreferences.edit().putInt("theme", 0).apply()
                }
                R.id.dark_theme_button->{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    sharedPreferences.edit().putInt("theme", 1).apply()
                }
                R.id.system_theme_button->{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    sharedPreferences.edit().putInt("theme", 2).apply()
                }
            }
        }
        binding.close.setOnClickListener {
            navController.popBackStack()
        }
    }

}