package ru.myitschool.todo.ui.settings_fragment.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdk
import com.yandex.authsdk.YandexAuthToken
import com.yandex.authsdk.internal.strategy.LoginType
import kotlinx.coroutines.launch
import ru.myitschool.todo.App
import ru.myitschool.todo.R
import ru.myitschool.todo.data.repository.SharedPreferencesRepository
import ru.myitschool.todo.databinding.FragmentSettingsBinding
import ru.myitschool.todo.ui.ViewModelFactory
import ru.myitschool.todo.utils.Constants
import javax.inject.Inject


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val navController: NavController by lazy {
        NavHostFragment.findNavController(this)
    }
    private val sdk: YandexAuthSdk by lazy {
        YandexAuthSdk(requireContext(), YandexAuthOptions(requireContext()))
    }
    private val viewModel: SettingsViewModel by viewModels {
        ViewModelFactory {
            (requireActivity().application as App).getAppComponent().settingViewModel()
        }
    }

    @Inject
    lateinit var sharedRepository: SharedPreferencesRepository

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
        setupThemeButtons()
        binding.close.setOnClickListener {
            navController.popBackStack()
        }
        binding.yandexLoginButton.setOnClickListener {
            runYandexAuth()
        }
        binding.yandexLogoutButton.setOnClickListener {
            viewModel.logout()
        }
        observeViewModel()
    }
    private fun setupThemeButtons(){
        when (sharedRepository.getTheme()) {
            0 -> {
                binding.themeSelector.check(R.id.light_theme_button)
            }

            1 -> {
                binding.themeSelector.check(R.id.dark_theme_button)
            }

            2 -> {
                binding.themeSelector.check(R.id.system_theme_button)
            }
        }
        binding.themeSelector.jumpDrawablesToCurrentState()
        binding.themeSelector.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.light_theme_button -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    sharedRepository.setTheme(0)
                }

                R.id.dark_theme_button -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    sharedRepository.setTheme(1)
                }

                R.id.system_theme_button -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    sharedRepository.setTheme(2)
                }
            }
        }
    }
    private fun observeViewModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.token.collect {
                    if (it != Constants.DEFAULT_TOKEN) {
                        binding.yandexLoginButton.visibility = View.GONE
                        binding.yandexLogoutButton.visibility = View.VISIBLE
                    } else {
                        binding.yandexLoginButton.visibility = View.VISIBLE
                        binding.yandexLogoutButton.visibility = View.GONE
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.accountInfo.collect {
                    if (it != null) {
                        binding.loggedName.text = it
                    } else {
                        binding.loggedName.text = ""
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collect {
                    if (it) {
                        Snackbar.make(
                            binding.yandexLogoutButton,
                            resources.getString(R.string.no_connection),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
    private fun runYandexAuth() {
        val intent: Intent = sdk.createLoginIntent(
            YandexAuthLoginOptions.Builder().setLoginType(LoginType.NATIVE).build()
        )
        someActivityResultLauncher.launch(intent)
    }

    private var someActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            try {
                val resultCode = result.resultCode
                val data = result.data
                val token: YandexAuthToken? = sdk.extractToken(resultCode, data)
                if (token != null) {
                    viewModel.login(token)
                }
            } catch (e: Exception) {
                Log.e("SettingFragment", e.toString())
            }
        }

}