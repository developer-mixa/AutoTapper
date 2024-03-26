package com.example.autotapper.presentation.fragments

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils.SimpleStringSplitter
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.autotapper.R
import com.example.autotapper.databinding.FragmentMainBinding
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
class MainFragment : Fragment(R.layout.fragment_main) {

    @Inject
    lateinit var factory: MainViewModel.Factory


    private val viewModel: MainViewModel by screenViewModel {
        factory.create(getMainNavigator(), getBaseScreen())
    }

    private val binding by viewBinding<FragmentMainBinding>()


    private val requestOverlayPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            val messageId =
                if (Settings.canDrawOverlays(requireContext())) R.string.permission_is_granted else
                    R.string.permission_is_denied

            showToast(getString(messageId))
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
            } else if (!isAccessibilitySettingsOn()) {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            } else {
                viewModel.startService(editTextClickSpeed.text.toString())
            }
        }

        buttonStop.setOnClickListener {
            TapperButtonService.stop(nestedActivity)
            TouchCatcherService.instance?.disable()
        }
    }

    private fun showPermissionDialog() {
        val alert = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.required_above_other_apps))
            .setMessage(getString(R.string.above_other_apps_message))
            .setPositiveButton(
                getString(R.string.settings)
            ) { dialog, _ ->
                launchSettings()
                dialog.cancel()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
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

    private fun isAccessibilitySettingsOn(): Boolean {
        val context = nestedActivity.applicationContext

        var accessibilityEnabled = 0
        val serviceName =
            nestedActivity.packageName + "/" + TouchCatcherService::class.java.canonicalName
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                context.applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }

        /**We will receive all enabled accessibility services, we use this to separate this by ":"*/
        val stringSplitter = SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {

            /**Receive all enabled accessibility services*/
            val settingValue = Settings.Secure.getString(
                context.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                stringSplitter.setString(settingValue)

                /**Here we find our service*/
                while (stringSplitter.hasNext()) {
                    val accessibilityService = stringSplitter.next()
                    if (accessibilityService.equals(serviceName, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }
        return false
    }


}