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

    /**
     * adds a user's click to the click list
     */
    override fun addTouch(touch: Touch) {
        if(choosing){
            Log.d("MyLog", "added $touch")
            allTouches.add(touch)
        }
    }

    /**
     * Remove all the touches
     */
    override fun removeAllTouches() {
        allTouches.clear()
    }

    /**
     * Returns all current touches
     */
    override fun getAllTouches(): List<Touch> {
        return allTouches
    }

    /**
     * Changes a choosing state
     */
    override fun refreshChoose() {
        choosing = !choosing

    }


}