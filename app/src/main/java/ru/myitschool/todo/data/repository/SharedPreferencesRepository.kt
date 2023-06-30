package ru.myitschool.todo.data.repository

import android.content.SharedPreferences
import ru.myitschool.todo.di.scopes.AppScope
import javax.inject.Inject

@AppScope
class SharedPreferencesRepository @Inject constructor(private val sharedPreferences: SharedPreferences) {
    fun writeRevision(revision: Int) {
        sharedPreferences.edit().putInt("revision", revision).apply()
    }

    fun getRevision() = sharedPreferences.getInt("revision", 0)
    fun getTheme() = sharedPreferences.getInt("theme",2)
    fun setTheme(theme:Int) = sharedPreferences.edit().putInt("theme", theme).apply()
    fun setAuthToken(token:String) = sharedPreferences.edit().putString("token", token).apply()
    fun getAuthToken() = sharedPreferences.getString("token", "")
}