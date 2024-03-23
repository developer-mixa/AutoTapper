package com.example.autotapper.presentation.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils.SimpleStringSplitter
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.example.autotapper.R
import com.example.autotapper.databinding.FragmentMainBinding
import com.example.autotapper.navigation.BaseFragment
import com.example.autotapper.navigation.BaseScreen
import com.example.autotapper.navigation.getBaseScreen
import com.example.autotapper.navigation.getMainNavigator
import com.example.autotapper.navigation.screenViewModel
import com.example.autotapper.presentation.fragments.viewmodels.MainViewModel
import com.example.autotapper.presentation.services.TapperButtonService
import com.example.autotapper.presentation.services.TouchCatcherService
import com.example.autotapper.utils.nestedActivity
import com.example.autotapper.utils.showToast
import com.example.autotapper.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainFragment : BaseFragment(R.layout.fragment_main) {

    @Inject
    lateinit var factory: MainViewModel.Factory

    @Inject
    lateinit var touchCatcherService: TouchCatcherService

    override val viewModel: MainViewModel by screenViewModel {
        factory.create(getMainNavigator(), getBaseScreen())
    }

    private val binding by viewBinding<FragmentMainBinding>()


    private val requestOverlayPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (Settings.canDrawOverlays(requireContext())) {
                showToast("Permission is granted!")
            } else {
                showToast("Permission is denied")
            }
        }

    class Screen : BaseScreen

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.tryLaunchStartFragment()
        onClickListeners()

    }

    private fun onClickListeners() = with(binding) {
        buttonStart.setOnClickListener {
            if (!Settings.canDrawOverlays(requireContext())) {
                showPermissionDialog()
            } else if (!isAccessibilitySettingsOn(nestedActivity.applicationContext)) {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            } else {
                TapperButtonService.start(nestedActivity)
            }
        }

        buttonStop.setOnClickListener {
            TapperButtonService.stop(nestedActivity)
            touchCatcherService.disable()
        }
    }

    private fun showPermissionDialog() {
        val alert = AlertDialog.Builder(requireContext())
            .setTitle("Требуется разрешение \"Поверх других приложений\"")
            .setMessage("Данное разрешение требуется, для настройки экрана под ваши нужды")
            .setPositiveButton(
                "Настройки"
            ) { dialog, _ ->
                launchSettings()
                dialog.cancel()
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }
            .create()
        alert.show()
    }

    private fun launchSettings() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${requireActivity().packageName}")
        )
        requestOverlayPermissionLauncher.launch(intent)
    }

    private fun isAccessibilitySettingsOn(mContext: Context): Boolean {
        var accessibilityEnabled = 0
        val service: String =
            nestedActivity.packageName + "/" + TouchCatcherService::class.java.canonicalName
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                mContext.applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }
        val mStringColonSplitter = SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(
                mContext.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }
        return false
    }


}