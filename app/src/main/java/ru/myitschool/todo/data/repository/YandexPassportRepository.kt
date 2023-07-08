package ru.myitschool.todo.data.repository

import ru.myitschool.todo.data.data_sources.network.yandex_passport.entities.InfoResponse

interface YandexPassportRepository {
    suspend fun getInfo(): InfoResponse?
}