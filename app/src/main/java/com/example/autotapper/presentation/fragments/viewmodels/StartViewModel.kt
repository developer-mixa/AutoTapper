package com.example.autotapper.presentation.fragments.viewmodels

import androidx.lifecycle.ViewModel
import com.example.autotapper.navigation.BaseScreen
import com.example.autotapper.navigation.Navigator
import com.example.autotapper.presentation.fragments.MainFragment
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class StartViewModel @AssistedInject constructor (
    @Assisted private val navigator: Navigator,
    @Assisted val screen: BaseScreen,
): ViewModel() {

    /**
     * Just start main fragment
     */
    fun launchMainScreen() = navigator.launch(MainFragment.Screen())

    @AssistedFactory
    interface Factory{
        fun create(navigator: Navigator, screen: BaseScreen): StartViewModel
    }

}