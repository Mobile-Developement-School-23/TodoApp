package ru.myitschool.todo.ui.activity

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.myitschool.todo.data.repository.SharedPreferencesRepository
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(private val sharedPreferencesRepository: SharedPreferencesRepository) :
    ViewModel() {
        private val _isGranted = MutableStateFlow<Boolean?>(null)
    val isGranted: StateFlow<Boolean?> get() = _isGranted
    init {
        _isGranted.value = sharedPreferencesRepository.readNotificationPermission()
    }
    fun savePermissionResult(isGranted: Boolean) {
        sharedPreferencesRepository.writeNotificationPermission(isGranted = isGranted)
        _isGranted.value = isGranted
    }
}