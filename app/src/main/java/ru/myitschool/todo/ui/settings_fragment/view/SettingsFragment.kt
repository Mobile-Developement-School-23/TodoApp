package ru.myitschool.todo.ui.settings_fragment.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import ru.myitschool.todo.App
import ru.myitschool.todo.R
import ru.myitschool.todo.data.repository.SharedPreferencesRepository
import ru.myitschool.todo.databinding.FragmentSettingsBinding
import javax.inject.Inject


class SettingsFragment : Fragment() {

    private var _binding:FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val navController: NavController by lazy {
        NavHostFragment.findNavController(this)
    }

    @Inject
    lateinit var sharedRepository:SharedPreferencesRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity().application as App).getAppComponent().inject(this)
        when(sharedRepository.getTheme()){
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
                    sharedRepository.setTheme(0)
                }
                R.id.dark_theme_button->{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    sharedRepository.setTheme(1)
                }
                R.id.system_theme_button->{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    sharedRepository.setTheme(2)
                }
            }
        }
        binding.close.setOnClickListener {
            navController.popBackStack()
        }
    }

}