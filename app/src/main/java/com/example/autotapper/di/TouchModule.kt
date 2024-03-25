package com.example.autotapper.di

import com.example.autotapper.data.InMemoryTouchRepository
import com.example.autotapper.domain.TouchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface TouchModule {

    @Binds
    fun bindTouchRepository(imMemoryTouchRepository: InMemoryTouchRepository) : TouchRepository

}