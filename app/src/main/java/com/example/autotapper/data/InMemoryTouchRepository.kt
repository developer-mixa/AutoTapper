package com.example.autotapper.data

import com.example.autotapper.domain.models.Touch
import com.example.autotapper.domain.repositories.TouchRepository
import com.example.autotapper.domain.repositories.TouchStateRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryTouchRepository @Inject constructor(
    private val logger: Logger,
    private val touchStateRepository: TouchStateRepository
) : TouchRepository {

    private val allTouches: MutableList<Touch> = mutableListOf()

    /**
     * adds a user's click to the click list
     */
    override fun addTouch(touch: Touch) {
        if(touchStateRepository.isChoosing()){
            logger.log("Touch was added.")
            allTouches.add(touch)
        }
    }

    /**
     * Remove all the touches
     */
    override fun removeAllTouches() {
        allTouches.clear()
    }

    /**
     * Returns all current touches
     */
    override fun getAllTouches(): List<Touch> {
        return allTouches
    }

}