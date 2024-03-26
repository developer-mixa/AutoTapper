package com.example.autotapper.presentation.custom_views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.util.AttributeSet
import android.widget.Checkable
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.autotapper.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Collections


class DropDownFloatingActionButton @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FloatingActionButton(context, attr, defStyleAttr), Checkable {

    private var checked = false
    private val animatorSet: AnimatorSet

    init {

        val default = context.getColor(R.color.dropDownButtonBackground)
        val white = context.getColor(R.color.white)

        val rotateAnim = ObjectAnimator.ofFloat(this, "rotation", 135f)

        val dfAnim = ValueAnimator.ofArgb(default, white)
        dfAnim.addUpdateListener {
            imageTintList = ColorStateList.valueOf(it.animatedValue as Int)
        }

        val fmAnim = ValueAnimator.ofArgb(white, default)
        fmAnim.addUpdateListener {
            backgroundTintList = ColorStateList.valueOf(it.animatedValue as Int)
        }

        animatorSet = AnimatorSet().apply {
            interpolator = FastOutSlowInInterpolator()
            playTogether(rotateAnim, dfAnim, fmAnim)
        }

    }

    override fun performClick(): Boolean {
        toggle()
        return super.performClick()
    }

    override fun setChecked(check: Boolean) {
        if(checked == check) return
        checked = check
        playAnimation()
    }

    override fun isChecked(): Boolean = checked

    override fun toggle() {
        isChecked = !checked
    }

    private fun playAnimation(){
        if(isChecked) animatorSet.start() else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            animatorSet.reverse()
        } else{
            reverseSequentialAnimatorSet(animatorSet).start()
        }
    }
    private fun reverseSequentialAnimatorSet(animatorSet: AnimatorSet): AnimatorSet {
        val animators = animatorSet.childAnimations
        animators.reverse()
        val reversedAnimatorSet = AnimatorSet()
        reversedAnimatorSet.playSequentially(animators)
        reversedAnimatorSet.duration = animatorSet.duration
        reversedAnimatorSet.interpolator = animatorSet.interpolator
        return reversedAnimatorSet
    }

}