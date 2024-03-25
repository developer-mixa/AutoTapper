package com.example.autotapper.presentation.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import com.example.autotapper.data.InMemoryTouchRepository
import com.example.autotapper.domain.Touch
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class TouchCatcherService @Inject constructor() : AccessibilityService() {

    @Inject
    lateinit var touchRepository: InMemoryTouchRepository

    private var job: Job? = null

    @RequiresApi(34)
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED && !touchRepository.performing) {

            val sourceNode = event.source
            if (sourceNode != null) {
                val bounds = Rect()
                sourceNode.getBoundsInScreen(bounds)

                val left = bounds.left
                val top = bounds.top
                val right = bounds.right
                val bottom = bounds.bottom

                if (touchRepository.choosing) {
                    touchRepository.addTouch(Touch(bounds.left.toFloat(), bounds.top.toFloat()))
                }

                Log.d("MyLog", "left $left top $top right $right bottom $bottom")
                sourceNode.recycle()
            }
        }


    }


    private fun createClick(x: Float, y: Float): GestureDescription {
        val path = Path().apply {
            moveTo(x, y)
        }
        val strokeDescription = GestureDescription.StrokeDescription(path, 0, 1)
        return GestureDescription.Builder().addStroke(strokeDescription).build()
    }

    private suspend fun performClick(x: Float, y: Float) = withContext(Dispatchers.Main) {
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

    fun startTapping() {
        //I'm re-assigning the job, because after cancel(), the coroutine is not available
        val touches = touchRepository.getAllTouches()
        job = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                if (touchRepository.performing) {
                    touches.forEach {
                        performClick(it.x, it.y)
                        delay(100)
                    }
                }
            }
        }
    }

    fun stopTapping() {
        job?.cancel()
    }

    override fun onInterrupt() {
        Log.d("MyLog", "something went wrong...")
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

        job?.start()

        Log.d("MyLog", "connected!")
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
