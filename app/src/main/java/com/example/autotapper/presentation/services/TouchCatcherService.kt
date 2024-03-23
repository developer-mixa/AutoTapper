package com.example.autotapper.presentation.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.graphics.Rect
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class TouchCatcherService @Inject constructor(

) : AccessibilityService() {

    @RequiresApi(34)
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            val sourceNode = event.source
            if (sourceNode != null) {
                val bounds = Rect()
                sourceNode.getBoundsInScreen(bounds)

                val left = bounds.left
                val top = bounds.top
                val right = bounds.right
                val bottom = bounds.bottom

                Log.d("MyLog", "left $left top $top right $right bottom $bottom")
                sourceNode.recycle()
            }
        }
    }

    fun disable() = stopSelf()


    override fun onInterrupt() {
       Log.d("MyLog", "something went wrong...")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        val info = AccessibilityServiceInfo()

        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED or AccessibilityEvent.TYPE_VIEW_FOCUSED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN
        info.notificationTimeout = 100

        this.serviceInfo = info

        Log.d("MyLog", "connected!")
    }


}
