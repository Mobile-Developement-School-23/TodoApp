package ru.myitschool.todo.data.repository.impl

import android.content.SharedPreferences
import ru.myitschool.todo.data.repository.SharedPreferencesRepository
import ru.myitschool.todo.di.scopes.AppScope
import ru.myitschool.todo.utils.Constants
import javax.inject.Inject

@AppScope
class SharedPreferencesRepositoryImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    SharedPreferencesRepository {
    override fun setRevision(revision: Int) {
        sharedPreferences.edit().putInt("revision", revision).apply()
    }

    override fun getRevision() = sharedPreferences.getInt("revision", 0)
    override fun getTheme() = sharedPreferences.getInt("theme", 2)
    override fun setTheme(theme: Int) = sharedPreferences.edit().putInt("theme", theme).apply()
    override fun setAuthToken(token: String) =
        sharedPreferences.edit().putString("token", token).apply()

    override fun getAuthToken() = sharedPreferences.getString("token", Constants.DEFAULT_TOKEN)
}