package com.example.autotapper.presentation.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import com.example.autotapper.data.Logger
import com.example.autotapper.domain.models.Touch
import com.example.autotapper.domain.repositories.TouchRepository
import com.example.autotapper.domain.repositories.TouchStateRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class TouchCatcherService @Inject constructor() : AccessibilityService() {

    @Inject
    lateinit var touchRepository: TouchRepository

    @Inject
    lateinit var touchStateRepository: TouchStateRepository

    @Inject
    lateinit var logger: Logger

    private var serviceJob = Job()
    private var serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)

    @RequiresApi(34)
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED && !touchStateRepository.isPerforming()) {

            val sourceNode = event.source
            if (sourceNode != null) {
                val bounds = Rect()
                sourceNode.getBoundsInScreen(bounds)
                if (touchStateRepository.isChoosing()) {
                    touchRepository.addTouch(Touch(bounds.left.toFloat(), bounds.top.toFloat()))
                }
            }
        }
    }


    private fun createClick(x: Float, y: Float): GestureDescription {
        val gesturePath = Path().apply {
            moveTo(x, y)
        }
        val strokeDescription = GestureDescription.StrokeDescription(gesturePath, 0, 1)
        return GestureDescription.Builder().addStroke(strokeDescription).build()
    }

    private suspend fun performClick(x: Float, y: Float) = withContext(Dispatchers.Main) {
        //description of the click
        val gesture = createClick(x, y)
        val callback = object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription) {
                super.onCompleted(gestureDescription)
            }

            override fun onCancelled(gestureDescription: GestureDescription) {
                super.onCancelled(gestureDescription)
            }
        }
        dispatchGesture(gesture, callback, null)
    }


    fun disable() {
        stopTapping()
        stopSelf()
    }

    fun refreshTapping(speed: Long){
        touchStateRepository.refreshPerforming()
        if(touchStateRepository.isPerforming()) startTapping(speed) else stopTapping()
    }

    private fun startTapping(speed: Long) {
        //I'm re-assigning the job, because after cancel(), the coroutine is not available

        serviceJob = Job()
        serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)

        val touches = touchRepository.getAllTouches()
        serviceScope.launch {
            while (isActive) {
                if (touchStateRepository.isPerforming()) {
                    touches.forEach {
                        delay(speed)
                        performClick(it.x, it.y)
                    }
                }
            }
        }
    }

    private fun stopTapping() {
        serviceJob.cancel()
    }

    override fun onInterrupt() {
        logger.log("Something went wrong...")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        _instance = this
        val info = AccessibilityServiceInfo()

        info.eventTypes =
            AccessibilityEvent.TYPE_VIEW_CLICKED or AccessibilityEvent.TYPE_VIEW_FOCUSED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN
        info.notificationTimeout = 100

        this.serviceInfo = info

        logger.log("connected!")
    }

    override fun onDestroy() {
        super.onDestroy()
        _instance = null
    }

    companion object {
        private var _instance: TouchCatcherService? = null

        val instance: TouchCatcherService?
            get() {
                return _instance
            }

    }


}
