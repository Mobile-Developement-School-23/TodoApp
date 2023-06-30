package ru.myitschool.todo.data.data_sources

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor : Interceptor {
    private val token = "Bearer astely"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val authRequest:Request = request.newBuilder().header("Authorization", token).build()

        return chain.proceed(authRequest)
    }
}