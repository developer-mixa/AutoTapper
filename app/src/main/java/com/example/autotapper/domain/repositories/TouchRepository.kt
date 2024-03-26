package com.example.autotapper.domain.repositories

import com.example.autotapper.domain.models.Touch

interface TouchRepository {
    fun addTouch(touch: Touch)

    fun removeAllTouches()

    fun getAllTouches(): List<Touch>
}