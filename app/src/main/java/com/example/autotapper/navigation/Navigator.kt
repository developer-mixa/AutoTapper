package com.example.autotapper.navigation

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity

interface Navigator {

    fun launch(screen: BaseScreen, addToBackStack: Boolean = false, aboveAll: Boolean = false)

    fun goBack()

    fun toast(@StringRes messageRes: Int)

    fun toast(messageString: String)

    fun activityScope(block: (AppCompatActivity) -> Any)

}