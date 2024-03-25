package com.example.autotapper.data

import android.util.Log
import com.example.autotapper.domain.Touch
import com.example.autotapper.domain.TouchRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryTouchRepository @Inject constructor() : TouchRepository {

    private val allTouches: MutableList<Touch> = mutableListOf()
    var choosing = false
    var performing = false

    override fun addTouch(touch: Touch) {
        if(choosing){
            Log.d("MyLog", "added $touch")
            allTouches.add(touch)
        }
    }

    override fun removeAllTouches() {
        allTouches.clear()
    }

    override fun getAllTouches(): List<Touch> {
        return allTouches
    }

    override fun refreshChoose() {
        choosing = !choosing

    }


}