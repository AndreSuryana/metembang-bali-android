package com.andresuryana.metembangbali.ui.main.profile.password

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.databinding.ActivityChangePasswordBinding
import com.andresuryana.metembangbali.dialog.LoadingDialogFragment
import com.andresuryana.metembangbali.helper.Helpers
import com.andresuryana.metembangbali.utils.event.ChangePasswordEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.*
import kotlin.concurrent.schedule

@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {

    // Layout binding
    private lateinit var binding: ActivityChangePasswordBinding

    // View model
    private val viewModel: ChangePasswordViewModel by viewModels()

    // Loading dialog
    private val loadingDialog = LoadingDialogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observe change password
        lifecycleScope.launchWhenStarted {
            viewModel.changePassword.collectLatest { changePasswordObserver(it) }
        }

        // Setup button listener
        setupButtonListener()
    }

    private fun setupButtonListener() {
        // Button save listener
        binding.btnSave.setOnClickListener {
            validated { oldPassword, newPassword, confirmPassword ->
                viewModel.changePassword(oldPassword, newPassword, confirmPassword)
            }
        }

        // Button back listener
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun validated(callback: (oldPassword: String, newPassword: String, confirmPassword: String) -> Unit) {

        // Get value
        val oldPassword = binding.etOldPassword.text?.trim().toString()
        val newPassword = binding.etNewPassword.text?.trim().toString()
        val confirmPassword = binding.etConfirmPassword.text?.trim().toString()

        // Reset helper text
        binding.apply {
            tilOldPassword.helperText = ""
            tilNewPassword.helperText = ""
            tilConfirmPassword.helperText = ""
        }

        // Validation
        if (oldPassword.isBlank()) {
            binding.tilOldPassword.apply {
                helperText = getString(R.string.helper_empty_old_password)
                requestFocus()
            }
            return
        }

        if (newPassword.isBlank()) {
            binding.tilNewPassword.apply {
                helperText = getString(R.string.helper_empty_new_password)
                requestFocus()
            }
            return
        }

        if (confirmPassword.isBlank()) {
            binding.tilConfirmPassword.apply {
                helperText = getString(R.string.helper_empty_confirm_password)
                requestFocus()
            }
            return
        }

        if (newPassword != confirmPassword) {
            Helpers.snackBarError(
                binding.root,
                getString(R.string.error_password_not_match),
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        return callback.invoke(oldPassword, newPassword, confirmPassword)
    }

    private fun changePasswordObserver(event: ChangePasswordEvent) {
        when (event) {
            is ChangePasswordEvent.Success -> {
                loadingDialog.dismiss()
                Helpers.snackBarSuccess(
                    binding.root,
                    getString(R.string.success_change_password),
                    Snackbar.LENGTH_SHORT
                ).show()
                Timer().schedule(1000L) {
                    finish()
                }
            }
            is ChangePasswordEvent.Error -> {
                loadingDialog.dismiss()
                Helpers.snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is ChangePasswordEvent.NetworkError -> {
                loadingDialog.dismiss()
                Helpers.snackBarError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            is ChangePasswordEvent.Loading -> {
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