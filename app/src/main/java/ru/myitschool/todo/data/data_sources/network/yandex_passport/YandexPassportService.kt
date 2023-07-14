package ru.myitschool.todo.data.data_sources.network.yandex_passport

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import ru.myitschool.todo.data.data_sources.network.yandex_passport.entities.InfoResponse

interface YandexPassportService {
    @GET("info")
    suspend fun getInfo(@Header("Authorization") auth:String):Response<InfoResponse>
}