package com.example.autotapper.presentation.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.autotapper.R
import com.example.autotapper.databinding.DropDownFloatingButtonBinding
import com.example.autotapper.utils.initTranslationAnim


class TapperButtonService : Service(), OnTouchListener, View.OnClickListener {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var moving = false
    private var params: WindowManager.LayoutParams? = null

    private fun setupFabs(binding: DropDownFloatingButtonBinding) = with(binding){
        miniFab1.initTranslationAnim(-96f, -96f)
        miniFab2.initTranslationAnim(0f, -128f)
        miniFab3.initTranslationAnim(96f, -96f)

        mainFab.setOnClickListener {
            if(miniFab1.isOrWillBeHidden) miniFab1.show() else miniFab1.hide()
            if(miniFab2.isOrWillBeHidden) miniFab2.show() else miniFab2.hide()
            if(miniFab3.isOrWillBeHidden) miniFab3.show() else miniFab3.hide()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()

        Toast.makeText(this, "Service has start!", Toast.LENGTH_SHORT).show()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        overlayView = inflater.inflate(R.layout.drop_down_floating_button, null)
        setupFabs(DropDownFloatingButtonBinding.bind(overlayView))

        overlayView.setOnTouchListener(this)

        params = WindowManager.LayoutParams(
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

        windowManager.addView(overlayView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::overlayView.isInitialized) {
            windowManager.removeView(overlayView)
        }
    }



    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        if(params == null || view == null) return true

        view.performClick()

        when(event!!.action){
            MotionEvent.ACTION_DOWN -> {
                initialX = params!!.x
                initialY = params!!.y
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                moving = true
            }
            MotionEvent.ACTION_UP -> {
                moving = false
            }
            MotionEvent.ACTION_MOVE -> {
                params!!.x = (initialX + (event.rawX - initialTouchX)).toInt()
                params!!.y = (initialY + (event.rawY - initialTouchY)).toInt()
                windowManager.updateViewLayout(overlayView, params)
            }
        }

        return true
    }
    companion object{
        fun start(activity: AppCompatActivity){
            val intent = Intent(activity, TapperButtonService::class.java)
            activity.startService(intent)
        }
        fun stop(activity: AppCompatActivity){
            val intent = Intent(activity, TapperButtonService::class.java)
            activity.stopService(intent)
        }
    }

    override fun onClick(v: View?) {
        if(moving) return
    }
}
