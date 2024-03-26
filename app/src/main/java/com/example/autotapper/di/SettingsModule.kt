package com.example.autotapper.di

import com.example.autotapper.data.DefaultSettingsRepository
import com.example.autotapper.domain.repositories.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface SettingsModule {

    @Binds
    fun bindSettings(settingsRepository: DefaultSettingsRepository): SettingsRepository

}