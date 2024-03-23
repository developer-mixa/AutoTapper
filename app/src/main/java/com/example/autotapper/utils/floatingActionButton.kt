package com.example.autotapper.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.floatingactionbutton.FloatingActionButton

private fun FloatingActionButton.animateWithTransition(trX: Float, trY: Float){
    val objAnimator = ObjectAnimator.ofPropertyValuesHolder(
        this,
        PropertyValuesHolder.ofFloat("translationX", translationX, trX),
        PropertyValuesHolder.ofFloat("translationY", translationY, trY),
    ).apply {
        duration = 300
        interpolator = FastOutSlowInInterpolator()
    }
    objAnimator.start()
}

fun FloatingActionButton.initTranslationAnim(trX: Float, trY: Float){
    visibility = View.INVISIBLE
    addOnShowAnimationListener(object : AnimatorListenerAdapter(){
        override fun onAnimationStart(animation: Animator) {
            animateWithTransition(trX, trY)
            super.onAnimationStart(animation)
        }
    })
    addOnHideAnimationListener(object : AnimatorListenerAdapter(){
        override fun onAnimationStart(animation: Animator) {
            animateWithTransition(0f, 0f)
            super.onAnimationStart(animation)
        }
    })
}
