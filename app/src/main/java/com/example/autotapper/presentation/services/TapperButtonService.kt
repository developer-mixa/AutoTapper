package com.example.autotapper.presentation.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Debug
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.autotapper.R
import com.example.autotapper.data.InMemoryTouchRepository
import com.example.autotapper.databinding.DropDownFloatingButtonBinding
import com.example.autotapper.presentation.activities.MainActivity
import com.example.autotapper.utils.initTranslationAnim
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class TapperButtonService @Inject constructor() : Service(), OnTouchListener {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View

    @Inject
    lateinit var touchRepository: InMemoryTouchRepository

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var moving = false
    private var waitForChooseParams: WindowManager.LayoutParams? = null

    private lateinit var binding: DropDownFloatingButtonBinding


    private fun setupFabs() = with(binding) {
        fabRecord.initTranslationAnim(-96f, -96f)
        fabPlayStop.initTranslationAnim(0f, -128f)
        fabCleanTouches.initTranslationAnim(96f, -96f)

        mainFab.setOnClickListener {
            refreshFabs(fabRecord, fabPlayStop, fabCleanTouches)
        }

        mainFab.setOnLongClickListener {
            returnToApp()
            true
        }

    }

    private fun returnToApp() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun refreshFabs(vararg fabs: FloatingActionButton) {
        fabs.forEach { fab ->
            if (fab.isOrWillBeHidden) fab.show() else fab.hide()
        }
    }

    private fun fabClickListeners() = with(binding) {

        fabPlayStop.setOnClickListener {
            performTouches()
        }

        fabRecord.setOnClickListener {
            recordTouches()
        }

        fabCleanTouches.setOnClickListener {
            cleanTouches()
        }

    }

    private fun cleanTouches() = touchRepository.removeAllTouches()

    private fun performTouches() {
        touchRepository.performing = !touchRepository.performing

        if (touchRepository.performing) TouchCatcherService.instance?.startTapping() else TouchCatcherService.instance?.stopTapping()

        val imageResource =
            if (touchRepository.performing) R.drawable.ic_stop else R.drawable.ic_play

        binding.fabPlayStop.setImageResource(imageResource)
    }

    private fun recordTouches() {
        touchRepository.refreshChoose()

        val imageResource =
            if (touchRepository.choosing) R.drawable.ic_stop else R.drawable.ic_record

        binding.fabRecord.setImageResource(imageResource)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()

        Toast.makeText(this, "Service has start!", Toast.LENGTH_SHORT).show()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        overlayView = inflater.inflate(R.layout.drop_down_floating_button, null)
        binding = DropDownFloatingButtonBinding.bind(overlayView)

        setupFabs()
        fabClickListeners()

        overlayView.setOnTouchListener(this)

        waitForChooseParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 100
        }

        windowManager.addView(overlayView, waitForChooseParams)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::overlayView.isInitialized) {
            windowManager.removeView(overlayView)
        }
    }


    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        if (waitForChooseParams == null || view == null) return true

        view.performClick()

        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = waitForChooseParams!!.x
                initialY = waitForChooseParams!!.y
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                moving = true
            }

            MotionEvent.ACTION_UP -> {
                moving = false
            }

            MotionEvent.ACTION_MOVE -> {
                waitForChooseParams!!.x = (initialX + (event.rawX - initialTouchX)).toInt()
                waitForChooseParams!!.y = (initialY + (event.rawY - initialTouchY)).toInt()
                windowManager.updateViewLayout(overlayView, waitForChooseParams)
            }
        }

        return true
    }

    companion object {
        fun start(activity: AppCompatActivity) {
            val intent = Intent(activity, TapperButtonService::class.java)
            activity.startService(intent)
        }

        fun stop(activity: AppCompatActivity) {
            val intent = Intent(activity, TapperButtonService::class.java)
            activity.stopService(intent)
        }
    }

}
