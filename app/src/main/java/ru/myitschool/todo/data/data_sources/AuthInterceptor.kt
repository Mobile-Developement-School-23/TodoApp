package ru.myitschool.todo.data.data_sources

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import ru.myitschool.todo.utils.Constants

class AuthInterceptor(private val token:String = Constants.DEFAULT_TOKEN): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val authRequest:Request = request.newBuilder().header("Authorization", token).build()

        return chain.proceed(authRequest)
    }
}