package ru.myitschool.todo.ui.settings_fragment.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.authsdk.YandexAuthToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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
    private val _error = Channel<Boolean>()
    val error: Flow<Boolean> = _error.receiveAsFlow()

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
            repository.loadAllItems {
                if (it){
                    _token.value = ""
                    _accountInfo.value = ""
                    sharedPreferencesRepository.setAuthToken("")
                    launch (Dispatchers.IO){
                        _error.send(false)
                    }
                }
                else{
                    repository.login(_token.value)
                    launch (Dispatchers.IO){
                        _error.send(true)
                    }
                }
            }
        }
    }
    private fun getInfo(){
        viewModelScope.launch (Dispatchers.IO){
            val value = yandexPassportRepository.getInfo()?.login
            _accountInfo.value = value
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (_token.value.isNotEmpty()){
            repository.login(_token.value)
        }
    }
}