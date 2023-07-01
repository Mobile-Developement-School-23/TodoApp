package ru.myitschool.todo.data.repository

import ru.myitschool.todo.data.data_sources.network.yandex_passport.YandexPassportService
import ru.myitschool.todo.data.data_sources.network.yandex_passport.entities.InfoResponse
import javax.inject.Inject

class YandexPassportRepository @Inject constructor(
    private val retrofitService: YandexPassportService,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) {
    suspend fun getInfo():InfoResponse?{
        val value = sharedPreferencesRepository.getAuthToken()
        if (value != null) {
            return try {
                val result = retrofitService.getInfo(value)
                println(result)
                result.body()
            } catch (_:Exception){
                null
            }
        }
        return null
    }
}