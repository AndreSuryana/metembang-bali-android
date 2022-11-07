package com.andresuryana.metembangbali.ui.start

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import com.andresuryana.metembangbali.databinding.ActivityGetStartedBinding
import com.andresuryana.metembangbali.helper.ActivityHelper.setActivityFullscreen
import com.andresuryana.metembangbali.helper.ActivityHelper.setupFadeActivityTransition
import com.andresuryana.metembangbali.helper.AnimationHelper.animateSlideDown
import com.andresuryana.metembangbali.helper.AnimationHelper.animateSlideUp
import com.andresuryana.metembangbali.helper.AppStartHelper.APP_START_NORMAL
import com.andresuryana.metembangbali.helper.AppStartHelper.setStartMethod
import com.andresuryana.metembangbali.ui.auth.signin.SignInActivity
import dagger.hilt.android.AndroidEntryPoint
import android.util.Pair as UtilPair

@AndroidEntryPoint
class GetStartedActivity : AppCompatActivity() {

    // Layout binding
    private lateinit var binding: ActivityGetStartedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout
        binding = ActivityGetStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Show logo animation
        showLogo()

        // Show get started container animation
        showGetStarted()

        // Set start method to APP_START_NORMAL
        setStartMethod(this, APP_START_NORMAL)

        // Get started button listener
        binding.btnGetStarted.setOnClickListener {
            Intent(this, SignInActivity::class.java).also {
                startActivity(
                    it,
                    ActivityOptions.makeSceneTransitionAnimation(
                        this,
                        UtilPair.create(binding.ivLogoMetembang, ViewCompat.getTransitionName(binding.ivLogoMetembang)),
                        UtilPair.create(binding.ivScreen, ViewCompat.getTransitionName(binding.ivScreen))
                    ).toBundle()
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Set activity to fullscreen
        setActivityFullscreen(View.VISIBLE)

        // Fade transition on enter and exit
        setupFadeActivityTransition()
    }

    private fun showLogo() {
        // Set visibility
        binding.ivLogoMetembang.visibility = View.VISIBLE

        // Start animation
        binding.ivLogoMetembang.animateSlideDown(this)
    }

    private fun showGetStarted() {
        // Set visibility
        binding.getStartedContainer.visibility = View.VISIBLE

        // Start animation
        binding.getStartedContainer.animateSlideUp(this)
    }
}