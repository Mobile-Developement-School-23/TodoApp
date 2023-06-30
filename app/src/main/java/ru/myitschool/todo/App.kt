package ru.myitschool.todo

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import ru.myitschool.todo.data.repository.SharedPreferencesRepository
import ru.myitschool.todo.di.components.AppComponent
import ru.myitschool.todo.di.components.DaggerAppComponent
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class App: Application() {
    private lateinit var appComponent: AppComponent
    @Inject
    lateinit var sharedRepository: SharedPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(applicationContext)
        appComponent.inject(this)
        registerWorker()
        setTheme()
    }
    fun getAppComponent() = appComponent

    private fun registerWorker(){
        val worker = PeriodicWorkRequestBuilder<NetworkWorker>(8,TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueue(worker)
    }
    private fun setTheme(){
        when(sharedRepository.getTheme()){
            0->{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            1->{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            2->{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }
}