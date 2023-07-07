package ru.myitschool.todo.data.data_sources

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor(private val token:String = "Bearer astely"): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val authRequest:Request = request.newBuilder().header("Authorization", token).build()

        return chain.proceed(authRequest)
    }
}