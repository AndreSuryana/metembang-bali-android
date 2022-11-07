package com.andresuryana.metembangbali.helper

import android.app.Activity
import android.os.Build
import android.transition.Fade
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager

object ActivityHelper {

    @Suppress("DEPRECATION")
    fun Activity.setActivityFullscreen(navigationBarVisibility: Int = View.GONE) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d("Fullscreen", "running on R or above")
            window.insetsController?.apply {
                if (navigationBarVisibility == View.VISIBLE) hide(WindowInsets.Type.navigationBars())
                else show(WindowInsets.Type.navigationBars())
                hide(WindowInsets.Type.statusBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            Log.d("Fullscreen", "running on below R")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )

            if (navigationBarVisibility == View.VISIBLE) {
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            }
        }
    }

    fun Activity.setupFadeActivityTransition() {
        val fade = Fade()
        fade.excludeTarget(androidx.appcompat.R.id.action_bar_container, true)
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)

        window.enterTransition = fade
        window.exitTransition = fade
    }
}