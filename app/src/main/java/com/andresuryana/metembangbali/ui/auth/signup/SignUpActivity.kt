package com.andresuryana.metembangbali.ui.auth.signup

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.databinding.ActivitySignUpBinding
import com.andresuryana.metembangbali.dialog.LoadingDialogFragment
import com.andresuryana.metembangbali.helper.ActivityHelper.setActivityFullscreen
import com.andresuryana.metembangbali.helper.ActivityHelper.setupFadeActivityTransition
import com.andresuryana.metembangbali.helper.AnimationHelper.animateSlideUp
import com.andresuryana.metembangbali.helper.Helpers
import com.andresuryana.metembangbali.ui.auth.signin.SignInActivity
import com.andresuryana.metembangbali.ui.main.MainActivity
import com.andresuryana.metembangbali.utils.Ext.isEmail
import com.andresuryana.metembangbali.utils.SessionManager
import com.andresuryana.metembangbali.utils.event.AuthEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import android.util.Pair as UtilPair

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    // Layout binding
    private lateinit var binding: ActivitySignUpBinding

    // Session manager
    private lateinit var sessionManager: SessionManager

    // View model
    private val viewModel: SignUpViewModel by viewModels()

    // Loading dialog
    private lateinit var loadingDialog: LoadingDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init loading dialog
        loadingDialog = LoadingDialogFragment()

        // Init session manager
        sessionManager = SessionManager(this)

        // Show form container animation
        showFormContainer()

        // Setup edit text focus listener to hide and show navigation bar
        setupEditTextFocusListener()

        // Observe sign up response
        viewModel.signUpResponse.observe(this, this::signUpResponseObserver)

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
        // Complete name edit text focus listener
        binding.etCompleteName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) setActivityFullscreen()
            else setActivityFullscreen(View.VISIBLE)
        }

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
        // Button sign up listener
        binding.btnSignUp.setOnClickListener {
            validated { name, email, password ->
                // Sign up with view model!
                viewModel.signUp(name, email, password)
            }
        }

        // Button sign in listener
        binding.btnSignIn.setOnClickListener {
            Intent(this, SignInActivity::class.java).also {
                startActivity(
                    it,
                    ActivityOptions.makeSceneTransitionAnimation(
                        this,
                        UtilPair.create(binding.ivScreen, ViewCompat.getTransitionName(binding.ivScreen)),
                        UtilPair.create(binding.ivLogoMetembang, ViewCompat.getTransitionName(binding.ivLogoMetembang))
                    ).toBundle()
                )
            }
        }
    }

    private fun validated(callback: (name: String, email: String, password: String) -> Unit) {

        // Get value
        val name = binding.etCompleteName.text?.trim().toString()
        val email = binding.etEmail.text?.trim().toString()
        val password = binding.etPassword.text?.trim().toString()

        // Reset helper text
        binding.apply {
            tilCompleteName.helperText = ""
            tilEmail.helperText = ""
            tilPassword.helperText = ""
        }

        // Validation
        if (name.isBlank()) {
            binding.tilCompleteName.apply {
                helperText = getString(R.string.helper_empty_complete_name)
                requestFocus()
            }
            return
        }

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

        return callback.invoke(name, email, password)
    }

    private fun signUpResponseObserver(event: AuthEvent) {
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