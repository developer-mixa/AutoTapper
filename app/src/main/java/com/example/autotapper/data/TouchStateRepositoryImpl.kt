package com.example.autotapper.data

import com.example.autotapper.domain.repositories.TouchStateRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TouchStateRepositoryImpl @Inject constructor() : TouchStateRepository {
    private var choosing = false
    private var performing = false

    override fun refreshChoose() {
        choosing = !choosing
    }

    override fun refreshPerforming() {
        performing = !performing
    }

    override fun isChoosing(): Boolean = choosing

    override fun isPerforming(): Boolean = performing
}