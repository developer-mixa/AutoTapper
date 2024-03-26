package com.example.autotapper.presentation.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.autotapper.R
import com.example.autotapper.databinding.ActivityMainBinding
import com.example.autotapper.navigation.BaseFragment
import com.example.autotapper.navigation.MainNavigator
import com.example.autotapper.presentation.fragments.MainFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val navigator by viewModels<MainNavigator>{ ViewModelProvider.AndroidViewModelFactory(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null){
            navigator.launchFragment(this, MainFragment.Screen(), idContainer = R.id.fragmentMainContainer)
        }
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentCallbacks,false)
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentCallbacks)
    }

    private val fragmentCallbacks = object : FragmentManager.FragmentLifecycleCallbacks(){
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {

            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            val result = navigator.result.value ?: return
            if (f is BaseFragment){
                f.viewModel.onResult(result)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        navigator.whenActivityActive.mainActivity = this
    }

    override fun onPause() {
        super.onPause()
        navigator.whenActivityActive.mainActivity = null
    }

}