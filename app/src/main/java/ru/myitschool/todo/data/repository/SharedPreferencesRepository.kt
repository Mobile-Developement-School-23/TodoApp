package ru.myitschool.todo.data.repository

import android.content.SharedPreferences

class SharedPreferencesRepository(val sharedPreferences: SharedPreferences) {
    fun writeRevision(revision: Int) {
        sharedPreferences.edit().putInt("revision", revision).apply()
    }

    fun getRevision() = sharedPreferences.getInt("revision", 0)
}