package com.example.autotapper.domain

interface TouchRepository {
    fun addTouch(touch: Touch)

    fun removeAllTouches()

    fun getAllTouches(): List<Touch>

    fun refreshChoose()

}