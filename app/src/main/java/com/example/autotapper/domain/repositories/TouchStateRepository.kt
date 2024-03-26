package com.example.autotapper.domain.repositories

interface TouchStateRepository {
    fun refreshChoose()

    fun refreshPerforming()

    fun isChoosing(): Boolean

    fun isPerforming(): Boolean
}