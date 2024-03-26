package com.example.autotapper.presentation.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import com.example.autotapper.R
import com.example.autotapper.databinding.DropDownFloatingButtonBinding
import com.example.autotapper.domain.repositories.TouchStateRepository
import com.example.autotapper.domain.usecases.RemoveAllTouchesUseCase
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
    lateinit var removeAllTouchesUseCase: RemoveAllTouchesUseCase

    @Inject
    lateinit var touchStateRepository: TouchStateRepository


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
    private var clickSpeed: Int = 100


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

    private fun cleanTouches() = removeAllTouchesUseCase()

    private fun performTouches() = with(binding) {
        TouchCatcherService.instance?.refreshTapping(clickSpeed.toLong())

        fabPlayStop.updateButtonState(
            isStateActive = touchStateRepository.isPerforming(),
            activeImageResource = R.drawable.ic_stop,
            inactiveImageResource = R.drawable.ic_play
        )
    }

    private fun recordTouches() = with(binding) {
        touchStateRepository.refreshChoose()

        fabRecord.updateButtonState(
            isStateActive = touchStateRepository.isChoosing(),
            activeImageResource = R.drawable.ic_stop,
            inactiveImageResource = R.drawable.ic_record
        )
    }

    private fun FloatingActionButton.updateButtonState(
        isStateActive: Boolean,
        @DrawableRes activeImageResource: Int,
        @DrawableRes inactiveImageResource: Int,
    ) {
        val imageResource = if (isStateActive) activeImageResource else inactiveImageResource
        setImageResource(imageResource)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        clickSpeed = intent?.getIntExtra(CLICK_SPEED_KEY, 100) ?: 100
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("ClickableViewAccessibility", "InflateParams")
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else
                WindowManager.LayoutParams.TYPE_PHONE,
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
        fun start(activity: AppCompatActivity, speed: Int) {
            val intent = Intent(activity, TapperButtonService::class.java).apply {
                putExtra(CLICK_SPEED_KEY, speed)
            }
            activity.startService(intent)
        }

        fun stop(activity: AppCompatActivity) {
            val intent = Intent(activity, TapperButtonService::class.java)
            activity.stopService(intent)
        }

        private const val CLICK_SPEED_KEY = "click_speed_key"
    }

}
