package ru.myitschool.todo.di.modules

import android.content.Context
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdk
import dagger.Module
import dagger.Provides
import ru.myitschool.todo.di.scopes.FragmentScope

@Module
interface SettingsFragmentModule {
    companion object{
        @Provides
        @FragmentScope
        fun provideYandexAuthSdk(context:Context):YandexAuthSdk{
            return YandexAuthSdk(context, YandexAuthOptions(context))
        }
    }
}