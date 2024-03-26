package com.example.autotapper.presentation.fragments.viewmodels

import androidx.lifecycle.ViewModel
import com.example.autotapper.domain.exceptions.ExceedingClickRateException
import com.example.autotapper.domain.repositories.SettingsRepository
import com.example.autotapper.domain.usecases.CheckClickSpeedUseCase
import com.example.autotapper.navigation.BaseScreen
import com.example.autotapper.navigation.Navigator
import com.example.autotapper.presentation.fragments.StartFragment
import com.example.autotapper.presentation.services.TapperButtonService
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.lang.NumberFormatException


class MainViewModel  @AssistedInject constructor(
    private val settingsRepository: SettingsRepository,
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: BaseScreen,
    private val checkClickSpeedUseCase: CheckClickSpeedUseCase
): ViewModel() {

    /**
     * Launches start fragment if we start application for the first time
     *
     * @return -> is first entry
     * */
    fun tryLaunchStartFragment() : Boolean{
        val firstEntry = settingsRepository.isFirstEntry()
        if(firstEntry){
            navigator.launch(StartFragment.Screen())
            settingsRepository.disableFirstEntry()
       }
        return firstEntry
    }

    fun startService(speedText: String) = navigator.activityScope{
        try {
            val resultValue = if(speedText == "") 500 else speedText.toInt()
            TapperButtonService.start(it, resultValue)
        }catch (e: ExceedingClickRateException){
            navigator.toast("Click speed must be > 100 mc!")
        }catch (e: NumberFormatException){
            navigator.toast("Click must be a number!")
        }
    }

    fun stopService() = navigator.activityScope {
        TapperButtonService.stop(it)
    }


    @AssistedFactory
    interface Factory{
        fun create(navigator: Navigator, screen: BaseScreen) : MainViewModel
    }

}