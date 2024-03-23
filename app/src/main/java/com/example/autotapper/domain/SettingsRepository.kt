package com.example.autotapper.domain

interface SettingsRepository {

    fun isFirstEntry() : Boolean

    fun disableFirstEntry()

}