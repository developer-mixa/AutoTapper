package com.example.autotapper.navigation
import androidx.lifecycle.ViewModel


open class BaseViewModel: ViewModel() {

    /**
     * Makes it easy to get data from one ViewModel to another
     */
    open fun onResult(result: Any){

    }

}