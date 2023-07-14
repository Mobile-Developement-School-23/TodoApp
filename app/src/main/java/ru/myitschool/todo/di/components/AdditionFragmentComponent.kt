package ru.myitschool.todo.di.components

import dagger.Subcomponent
import ru.myitschool.todo.di.scopes.FragmentScope
import ru.myitschool.todo.ui.addition_fragment.AdditionViewModel

@FragmentScope
@Subcomponent
interface AdditionFragmentComponent {
    fun additionViewModel(): AdditionViewModel
}