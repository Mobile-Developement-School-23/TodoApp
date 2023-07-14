package ru.myitschool.todo.di.components

import dagger.Subcomponent
import ru.myitschool.todo.di.modules.SettingsFragmentModule
import ru.myitschool.todo.di.scopes.FragmentScope
import ru.myitschool.todo.ui.settings_fragment.view.SettingsFragment
import ru.myitschool.todo.ui.settings_fragment.view.SettingsViewModel

@Subcomponent(modules = [SettingsFragmentModule::class])
@FragmentScope
interface SettingsFragmentComponent {
    fun inject(fragment: SettingsFragment)
    fun settingViewModel(): SettingsViewModel
}