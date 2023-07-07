package ru.myitschool.todo.utils

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import ru.myitschool.todo.data.repository.SharedPreferencesRepository
import javax.inject.Inject

class NetworkInterceptor @Inject constructor(val repository: SharedPreferencesRepository):Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request:Request = chain.request().
        newBuilder()
            .addHeader("X-Last-Known-Revision", repository.getRevision().toString())
            .addHeader("Authorization", repository.getAuthToken()?:Constants.DEFAULT_TOKEN).build()
        val response = chain.proceed(request)
        if (response.isSuccessful){
            val result = response.peekBody(Long.MAX_VALUE).string()
            val gson = Gson()
            repository.setRevision(gson.fromJson(result, Revision::class.java).revision)
        }
        return response
    }
}