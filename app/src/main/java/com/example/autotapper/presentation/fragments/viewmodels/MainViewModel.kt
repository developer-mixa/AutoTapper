package com.example.autotapper.presentation.fragments.viewmodels

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Debug
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.autotapper.domain.SettingsRepository
import com.example.autotapper.navigation.BaseScreen
import com.example.autotapper.navigation.BaseViewModel
import com.example.autotapper.navigation.Navigator
import com.example.autotapper.presentation.fragments.StartFragment
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject


class MainViewModel  @AssistedInject constructor(
    private val settingsRepository: SettingsRepository,
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: BaseScreen,
): BaseViewModel() {

    fun tryLaunchStartFragment() : Boolean{
        val firstEntry = settingsRepository.isFirstEntry()
        if(firstEntry){
            navigator.launch(StartFragment.Screen())
            settingsRepository.disableFirstEntry()
       }
        return firstEntry
    }

    @AssistedFactory
    interface Factory{
        fun create(navigator: Navigator, screen: BaseScreen) : MainViewModel
    }

}