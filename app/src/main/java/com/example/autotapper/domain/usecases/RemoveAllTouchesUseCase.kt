package com.example.autotapper.domain.usecases

import com.example.autotapper.domain.repositories.TouchRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoveAllTouchesUseCase @Inject constructor(
    private val touchRepository: TouchRepository
) {
    operator fun invoke(){
        touchRepository.removeAllTouches()
    }
}