package com.andresuryana.metembangbali.helper

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import com.andresuryana.metembangbali.R

object AnimationHelper {

    private fun View.mainAnimate(
        context: Context,
        animRes: Int,
        enableInterpolator: Boolean = true,
        interpolatorRes: Int = android.R.anim.accelerate_decelerate_interpolator,
        duration: Long? = null
    ) {
        startAnimation(
            AnimationUtils.loadAnimation(context, animRes).apply {
                if (enableInterpolator) setInterpolator(context, interpolatorRes)
                if (duration != null) setDuration(duration)
            }
        )
    }

    fun View.animateFadeIn(
        context: Context,
        enableInterpolator: Boolean = true,
        interpolatorRes: Int = android.R.anim.accelerate_decelerate_interpolator,
        duration: Long? = null
    ) {
        mainAnimate(context, R.anim.fade_in, enableInterpolator, interpolatorRes, duration)
    }

    fun View.animateFadeOut(
        context: Context,
        enableInterpolator: Boolean = true,
        interpolatorRes: Int = android.R.anim.accelerate_decelerate_interpolator,
        duration: Long? = null
    ) {
        mainAnimate(context, R.anim.fade_out, enableInterpolator, interpolatorRes, duration)
    }

    fun View.animateSlideUp(
        context: Context,
        enableInterpolator: Boolean = true,
        interpolatorRes: Int = android.R.anim.accelerate_decelerate_interpolator,
        duration: Long? = null
    ) {
        mainAnimate(context, R.anim.slide_up, enableInterpolator, interpolatorRes, duration)
    }

    fun View.animateSlideDown(
        context: Context,
        enableInterpolator: Boolean = true,
        interpolatorRes: Int = android.R.anim.accelerate_decelerate_interpolator,
        duration: Long? = null
    ) {
        mainAnimate(context, R.anim.slide_down, enableInterpolator, interpolatorRes, duration)
    }

    fun View.animateSlide(
        currentHeight: Int,
        newHeight: Int,
        duration: Long = 500L
    ) {
        val animator = ValueAnimator
            .ofInt(currentHeight, newHeight)
            .setDuration(duration)

        animator.addUpdateListener {
            val height = it.animatedValue as Int
            layoutParams.height = height
            isVisible = height > 0
            requestLayout()
        }

        AnimatorSet().also {
            it.interpolator = AccelerateDecelerateInterpolator()
            it.play(animator)
            it.start()
        }
    }
}