package com.example.autotapper.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

fun provideAssistedFactory(create: () -> Any) : ViewModelProvider.Factory{
    return object : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return create() as T
        }
    }
}

inline fun <reified VM: ViewModel> Fragment.screenViewModel(crossinline create: () -> Any) = viewModels<VM>{
    provideAssistedFactory { create() }
}

fun Fragment.getMainNavigator(): MainNavigator {
    val hostActivity = requireActivity()
    val application = hostActivity.application
    val navigatorProvider = ViewModelProvider(hostActivity, ViewModelProvider.AndroidViewModelFactory(application))
    return navigatorProvider[MainNavigator::class.java]
}

fun Fragment.getBaseScreen() : BaseScreen{
    return requireArguments().getSerializable(ARG_SCREEN) as BaseScreen
}
