package com.andresuryana.metembangbali.ui.main.profile.edit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.databinding.ActivityEditProfileBinding
import com.andresuryana.metembangbali.dialog.LoadingDialogFragment
import com.andresuryana.metembangbali.helper.Helpers
import com.andresuryana.metembangbali.utils.Ext.isEmail
import com.andresuryana.metembangbali.utils.event.UserEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.Timer
import kotlin.concurrent.schedule

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    // Layout binding
    private lateinit var binding: ActivityEditProfileBinding

    // View model
    private val viewModel: EditProfileViewModel by viewModels()

    // Loading dialog
    private val loadingDialog = LoadingDialogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observe current user
        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest { userObserver(it) }
        }

        // Observe update user
        viewModel.updateUser.observe(this, this::updateUserObserver)

        // Setup button listener
        setupButtonListener()
    }

    private fun setupButtonListener() {
        // Button save
        binding.btnSave.setOnClickListener {
            validated { name, email, phone, occupation, address ->
                viewModel.updateUser(name, email, phone, address, occupation)
            }
        }

        // Button back listener
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun userObserver(event: UserEvent) {
        when (event) {
            is UserEvent.Success -> {
                // Fill user info
                binding.etCompleteName.setText(event.user.name)
                binding.etEmail.setText(event.user.email)
                binding.etPhone.setText(event.user.phone)
                binding.etOccupation.setText(event.user.occupation)
                binding.etAddress.setText(event.user.address)
            }
            is UserEvent.Error -> {
                Helpers.checkErrorState(binding.root, event.message)
                Helpers.snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is UserEvent.NetworkError -> {
                Helpers.snackBarNetworkError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ) {
                    viewModel.fetchUser()
                }.show()
            }
            is UserEvent.Loading -> {}
        }
    }

    private fun updateUserObserver(event: UserEvent) {
        when (event) {
            is UserEvent.Success -> {
                loadingDialog.dismiss()
                Helpers.snackBarSuccess(
                    binding.root,
                    getString(R.string.success_edit_profile),
                    Snackbar.LENGTH_SHORT
                ).show()
                Timer().schedule(1000L) {
                    finish()
                }
            }
            is UserEvent.Error -> {
                loadingDialog.dismiss()
                Helpers.checkErrorState(binding.root, event.message)
                Helpers.snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is UserEvent.NetworkError -> {
                loadingDialog.dismiss()
                Helpers.snackBarError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            is UserEvent.Loading -> {
                if (!loadingDialog.isAdded) {
                    loadingDialog.show(
                        supportFragmentManager,
                        LoadingDialogFragment::class.java.simpleName
                    )
                }
            }
        }
    }

    private fun validated(
        callback: (name: String, email: String, phone: String, occupation: String?, address: String) -> Unit
    ) {

        // Get value
        val name = binding.etCompleteName.text?.trim().toString()
        val email = binding.etEmail.text?.trim().toString()
        val phone = binding.etPhone.text?.trim().toString()
        val occupation = binding.etOccupation.text?.trim().toString().ifBlank { null }
        val address = binding.etAddress.text?.trim().toString()

        // Reset helper text
        binding.apply {
            tilCompleteName.helperText = ""
            tilEmail.helperText = ""
            tilPhone.helperText = ""
            tilOccupation.helperText = ""
            tilAddress.helperText = ""
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

        if (phone.isBlank()) {
            binding.tilPhone.apply {
                helperText = getString(R.string.helper_empty_phone)
                requestFocus()
            }
            return
        } else {
            if (!phone.matches("\\^[+][0-9]\$".toRegex())) {
                binding.tilPhone.apply {
                    helperText = getString(R.string.helper_invalid_phone_must_number)
                    requestFocus()
                }
                return
            }
            if (!phone.matches("\\^[+]{10,13}".toRegex())) {
                binding.tilPhone.apply {
                    helperText = getString(R.string.helper_invalid_phone_must_count)
                    requestFocus()
                }
                return
            }
        }

        if (address.isBlank()) {
            binding.tilAddress.apply {
                helperText = getString(R.string.helper_empty_address)
                requestFocus()
            }
            return
        }

        return callback.invoke(name, email, phone, occupation, address)
    }
}