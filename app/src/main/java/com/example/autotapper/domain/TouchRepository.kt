package com.example.autotapper.domain

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

interface TouchRepository {
    fun addTouch(touch: Touch)

    fun removeAllTouches()

    fun getAllTouches(): List<Touch>

    fun refreshChoose()

}