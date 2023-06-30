package ru.myitschool.todo.ui.settings_fragment.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.authsdk.YandexAuthToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.myitschool.todo.data.repository.SharedPreferencesRepository
import ru.myitschool.todo.data.repository.TodoItemsRepository
import ru.myitschool.todo.data.repository.YandexPassportRepository
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    private val repository: TodoItemsRepository,
    private val yandexPassportRepository: YandexPassportRepository
    ) :
    ViewModel() {

    private val _token = MutableStateFlow(sharedPreferencesRepository.getAuthToken()?:"")
    val token: StateFlow<String> = _token
    private val _accountInfo = MutableStateFlow<String?>(null)
    val accountInfo: StateFlow<String?> = _accountInfo

    init {
        getInfo()
    }
    fun login(token: YandexAuthToken){
        val newToken = "OAuth ${token.value}"
        sharedPreferencesRepository.setAuthToken(newToken)
        _token.value = newToken
        viewModelScope.launch(Dispatchers.IO) {
            repository.login(newToken)
            repository.loadAllItems()
        }
        getInfo()
    }
    fun logout(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.logout()
            repository.loadAllItems()
        }
        _token.value = ""
        _accountInfo.value = ""
        sharedPreferencesRepository.setAuthToken("")
    }
    private fun getInfo(){
        viewModelScope.launch (Dispatchers.IO){
            val value = yandexPassportRepository.getInfo()?.login
            println(value)
            _accountInfo.value = value
        }
    }
}