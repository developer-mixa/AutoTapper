package com.example.autotapper.presentation.fragments

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import com.example.autotapper.R
import com.example.autotapper.databinding.FragmentStartBinding
import com.example.autotapper.navigation.BaseScreen
import com.example.autotapper.navigation.getBaseScreen
import com.example.autotapper.navigation.getMainNavigator
import com.example.autotapper.navigation.screenViewModel
import com.example.autotapper.presentation.fragments.viewmodels.StartViewModel
import com.example.autotapper.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StartFragment : Fragment(R.layout.fragment_start) {

    class Screen: BaseScreen

    private val binding: FragmentStartBinding by viewBinding()

    @Inject lateinit var factory: StartViewModel.Factory

    private val viewModel: StartViewModel by screenViewModel {
        factory.create(getMainNavigator(), getBaseScreen())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startTextAnimation()
    }

    private fun startTextAnimation() = with(binding){
        startTextView.alpha = 0f

        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = START_TEXT_DURATION
            interpolator = LinearInterpolator()

            addUpdateListener { animation ->
                val alpha = animation.animatedValue as Float
                startTextView.alpha = alpha
            }

            addListener(onEnd = { viewModel.launchMainScreen() })
        }

        animator.start()
    }

    private companion object{
        private const val START_TEXT_DURATION = 3000L
    }

}