package com.example.autotapper.domain.repositories

interface SettingsRepository {

    fun isFirstEntry() : Boolean

    fun disableFirstEntry()

}