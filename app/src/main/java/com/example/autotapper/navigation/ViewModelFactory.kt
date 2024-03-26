package com.example.autotapper.navigation

import android.os.Build
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Function for creating custom assistedFactory
 *
 * @param create -> function which create ViewModel
 */
@Suppress("UNCHECKED_CAST")
fun provideAssistedFactory(create: () -> Any) : ViewModelProvider.Factory{
    return object : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return create() as T
        }
    }
}

/**
 * Function for fast creating ViewModel in some fragment
 *
 * @param create -> function which create ViewModel
 */
inline fun <reified VM: ViewModel> Fragment.screenViewModel(crossinline create: () -> Any) = viewModels<VM>{
    provideAssistedFactory { create() }
}

/**
 * Return navigator from main activity to fragment
 */
fun Fragment.getMainNavigator(): MainNavigator {
    val hostActivity = requireActivity()
    val application = hostActivity.application
    val navigatorProvider = ViewModelProvider(hostActivity, ViewModelProvider.AndroidViewModelFactory(application))
    return navigatorProvider[MainNavigator::class.java]
}

/**
 * Return BaseScreen in the fragment
 */
fun Fragment.getBaseScreen() : BaseScreen{
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        requireArguments().getSerializable(ARG_SCREEN, BaseScreen::class.java) as BaseScreen
    } else {
        requireArguments().getSerializable(ARG_SCREEN) as BaseScreen
    }
}
