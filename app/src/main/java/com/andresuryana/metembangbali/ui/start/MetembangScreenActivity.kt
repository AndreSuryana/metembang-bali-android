package com.andresuryana.metembangbali.ui.start

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.andresuryana.metembangbali.databinding.ActivityMetembangScreenBinding
import com.andresuryana.metembangbali.helper.ActivityHelper.setActivityFullscreen
import com.andresuryana.metembangbali.helper.ActivityHelper.setupFadeActivityTransition
import com.andresuryana.metembangbali.helper.AnimationHelper.animateFadeIn
import com.andresuryana.metembangbali.helper.AppStartHelper.APP_FIRST_START
import com.andresuryana.metembangbali.helper.AppStartHelper.getStartMethod
import com.andresuryana.metembangbali.ui.auth.signin.SignInActivity
import com.andresuryana.metembangbali.ui.main.MainActivity
import com.andresuryana.metembangbali.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import kotlin.concurrent.schedule

@AndroidEntryPoint
class MetembangScreenActivity : AppCompatActivity() {

    // Layout binding
    private lateinit var binding: ActivityMetembangScreenBinding

    // Session manager
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout
        binding = ActivityMetembangScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init session manager
        sessionManager = SessionManager(this)

        // Show animated logo
        binding.ivLogoMetembang.animateFadeIn(this)

        // Check user authentication
        Timer().schedule(2000) {
            if (getStartMethod(this@MetembangScreenActivity) == APP_FIRST_START) {
                // If first start, navigate to GetStartedActivity
                Intent(this@MetembangScreenActivity, GetStartedActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(it)
                }
            } else {
                Log.d("User", "role: ${sessionManager.getAuthStatus()}")
                // Otherwise, check user authentication status
                if (sessionManager.getAuthStatus() != null) {
                    // Navigate to main activity
                    Intent(this@MetembangScreenActivity, MainActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(it)
                    }
                } else {
                    // If there is no user authentication status, navigate to SignInActivity
                    Intent(this@MetembangScreenActivity, SignInActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(it)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Set activity to fullscreen
        this.setActivityFullscreen(View.VISIBLE)

        // Fade transition on enter and exit
        setupFadeActivityTransition()
    }
}