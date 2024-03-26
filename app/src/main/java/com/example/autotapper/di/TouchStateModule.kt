package com.example.autotapper.di

import com.example.autotapper.data.TouchStateRepositoryImpl
import com.example.autotapper.domain.repositories.TouchStateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface TouchStateModule {

    @Binds
    fun bindTouchStateRepository(touchStateRepositoryImpl: TouchStateRepositoryImpl) : TouchStateRepository

}