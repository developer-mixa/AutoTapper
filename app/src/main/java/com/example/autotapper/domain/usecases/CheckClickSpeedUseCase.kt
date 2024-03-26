package com.example.autotapper.domain.usecases

import com.example.autotapper.domain.exceptions.ExceedingClickRateException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckClickSpeedUseCase @Inject constructor() {

    @Throws(ExceedingClickRateException::class)
    operator fun invoke(speed: Int) : Int{
        if (speed < MINIMUM_SPEED) throw ExceedingClickRateException()
        return speed
    }

    private companion object{
        const val MINIMUM_SPEED = 100
    }
}