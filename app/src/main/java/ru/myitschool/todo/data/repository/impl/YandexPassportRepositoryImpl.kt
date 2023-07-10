package ru.myitschool.todo.data.repository.impl

import ru.myitschool.todo.data.data_sources.network.yandex_passport.YandexPassportService
import ru.myitschool.todo.data.data_sources.network.yandex_passport.entities.InfoResponse
import ru.myitschool.todo.data.repository.SharedPreferencesRepository
import ru.myitschool.todo.data.repository.YandexPassportRepository
import javax.inject.Inject

class YandexPassportRepositoryImpl @Inject constructor(
    private val retrofitService: YandexPassportService,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : YandexPassportRepository {
    override suspend fun getInfo(): InfoResponse? {
        val value = sharedPreferencesRepository.getAuthToken()
        if (value != null) {
            return try {
                val result = retrofitService.getInfo(value)
                result.body()
            } catch (_: Exception) {
                null
            }
        }
        return null
    }
}