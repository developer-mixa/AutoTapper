package com.example.autotapper.data

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Logger @Inject constructor() {

    fun log(message: String, tag: String = DEFAULT_TAG){
        Log.d(tag, message)
    }

    private companion object{
        const val DEFAULT_TAG = "MyLog"
    }
}