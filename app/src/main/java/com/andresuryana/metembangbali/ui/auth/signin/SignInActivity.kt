package com.andresuryana.metembangbali.ui.auth.signin

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.databinding.ActivitySignInBinding
import com.andresuryana.metembangbali.dialog.LoadingDialogFragment
import com.andresuryana.metembangbali.helper.ActivityHelper.setActivityFullscreen
import com.andresuryana.metembangbali.helper.ActivityHelper.setupFadeActivityTransition
import com.andresuryana.metembangbali.helper.AnimationHelper.animateSlideUp
import com.andresuryana.metembangbali.helper.Helpers
import com.andresuryana.metembangbali.ui.main.MainActivity
import com.andresuryana.metembangbali.ui.auth.signup.SignUpActivity
import com.andresuryana.metembangbali.utils.Ext.isEmail
import com.andresuryana.metembangbali.utils.SessionManager
import com.andresuryana.metembangbali.utils.event.AuthEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import android.util.Pair as UtilPair

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    // Layout binding
    private lateinit var binding: ActivitySignInBinding

    // Session manager
    private lateinit var sessionManager: SessionManager

    // View model
    private val viewModel: SignInViewModel by viewModels()

    // Loading dialog
    private lateinit var loadingDialog: LoadingDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init loading dialog
        loadingDialog = LoadingDialogFragment()

        // Init session manager
        sessionManager = SessionManager(this)

        // Show form container animation
        showFormContainer()

        // Setup edit text focus listener to hide and show navigation bar
        setupEditTextFocusListener()

        // Observe sign in response
        viewModel.signInResponse.observe(this, this::signInResponseObserver)

        // Setup button listener
        setupButtonListener()
    }

    override fun onResume() {
        super.onResume()

        // Set activity to fullscreen
        setActivityFullscreen(View.VISIBLE)

        // Fade transition on enter and exit
        setupFadeActivityTransition()
    }

    private fun showFormContainer() {
        // Set visibility
        binding.formContainer.visibility = View.VISIBLE

        // Start animation
        binding.formContainer.animateSlideUp(this, duration = 500L)
    }

    private fun setupEditTextFocusListener() {
        // Email edit text focus listener
        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) setActivityFullscreen()
            else setActivityFullscreen(View.VISIBLE)
        }

        // Password edit text focus listener
        binding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) setActivityFullscreen()
            else setActivityFullscreen(View.VISIBLE)
        }
    }

    private fun setupButtonListener() {
        // Button sign in listener
        binding.btnSignIn.setOnClickListener {
            validated { email, password ->
                // Sign in with view model
                viewModel.signIn(email, password)
            }
        }

        // Button sign in guest listener
        binding.btnSignInGuest.setOnClickListener {
            sessionManager.authenticateAsGuest()
            Intent(this, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(it)
            }
        }

        // Button sign up listener
        binding.btnSignUp.setOnClickListener {
            Intent(this, SignUpActivity::class.java).also {
                startActivity(
                    it,
                    ActivityOptions.makeSceneTransitionAnimation(
                        this,
                        UtilPair.create(
                            binding.ivScreen,
                            ViewCompat.getTransitionName(binding.ivScreen)
                        ),
                        UtilPair.create(
                            binding.ivLogoMetembang,
                            ViewCompat.getTransitionName(binding.ivLogoMetembang)
                        )
                    ).toBundle()
                )
            }
        }
    }

    private fun validated(callback: (email: String, password: String) -> Unit) {

        // Get value
        val email = binding.etEmail.text?.trim().toString()
        val password = binding.etPassword.text?.trim().toString()

        // Reset helper text
        binding.apply {
            tilEmail.helperText = ""
            tilPassword.helperText = ""
        }

        // Validation
        if (email.isBlank()) {
            binding.tilEmail.apply {
                helperText = getString(R.string.helper_empty_email)
                requestFocus()
            }
            return
        } else {
            if (!email.isEmail()) {
                binding.tilEmail.apply {
                    helperText = getString(R.string.helper_invalid_email)
                    requestFocus()
                }
                return
            }
        }

        if (password.isEmpty()) {
            binding.tilPassword.apply {
                helperText = getString(R.string.helper_empty_password)
                requestFocus()
            }
            return
        }

        return callback.invoke(email, password)
    }

    private fun signInResponseObserver(event: AuthEvent) {
        when (event) {
            is AuthEvent.Success -> {
                loadingDialog.dismiss()
                sessionManager.authenticateAsRegisteredUser(event.authResponse.token)
                Intent(this, MainActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(it)
                }
            }
            is AuthEvent.Error -> {
                loadingDialog.dismiss()
                Helpers.snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is AuthEvent.NetworkError -> {
                loadingDialog.dismiss()
                Helpers.snackBarError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            is AuthEvent.Loading -> {
                if (!loadingDialog.isAdded) {
                    loadingDialog.show(
                        supportFragmentManager,
                        LoadingDialogFragment::class.java.simpleName
                    )
                }
            }
        }
    }
}