package ru.myitschool.todo.data.repository

interface SharedPreferencesRepository {
    fun setRevision(revision: Int)
    fun getRevision():Int
    fun getTheme():Int
    fun setTheme(theme:Int)
    fun setAuthToken(token:String)
    fun getAuthToken():String?

    fun writeNotificationPermission(isGranted:Boolean)

    fun readNotificationPermission():Boolean?
}