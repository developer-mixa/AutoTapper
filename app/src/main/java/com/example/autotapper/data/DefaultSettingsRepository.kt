package com.example.autotapper.data

import android.content.Context
import android.content.SharedPreferences
import com.example.autotapper.domain.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

    override fun isFirstEntry(): Boolean {
        return sharedPreferences.getBoolean(FIRST_ENTRY_KEY, true)
    }

    override fun disableFirstEntry() {
        sharedPreferences.edit()
            .putBoolean(FIRST_ENTRY_KEY, false)
            .apply()
    }

    private companion object{
        const val APP_PREFERENCES = "app_preferences"
        const val FIRST_ENTRY_KEY = "first_entry"
    }

}