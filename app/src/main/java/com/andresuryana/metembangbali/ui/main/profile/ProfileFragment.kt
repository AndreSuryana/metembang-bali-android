package com.andresuryana.metembangbali.ui.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.databinding.FragmentProfileBinding
import com.andresuryana.metembangbali.dialog.LoadingDialogFragment
import com.andresuryana.metembangbali.helper.Helpers
import com.andresuryana.metembangbali.ui.auth.signin.SignInActivity
import com.andresuryana.metembangbali.ui.main.profile.edit.EditProfileActivity
import com.andresuryana.metembangbali.ui.main.profile.password.ChangePasswordActivity
import com.andresuryana.metembangbali.ui.main.profile.submission.UserSubmissionFragment
import com.andresuryana.metembangbali.utils.Constants.REGISTERED_USER
import com.andresuryana.metembangbali.utils.SessionManager
import com.andresuryana.metembangbali.utils.event.SignOutEvent
import com.andresuryana.metembangbali.utils.event.UserEvent
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    // Layout binding
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // View model
    private val viewModel: ProfileViewModel by viewModels()

    // Loading dialog
    private val loadingDialog = LoadingDialogFragment()

    // Session manager
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Init session manager
        sessionManager = SessionManager(requireContext())

        // Check authentication status from session manager
        fetchUserIfAuthenticated()

        // Observe user information
        viewModel.user.observe(viewLifecycleOwner, this::userObserver)

        // Observe sign out response
        if (sessionManager.getAuthStatus() == REGISTERED_USER) {
            lifecycleScope.launchWhenStarted {
                viewModel.signOut.collectLatest { signOutObserver(it) }
            }
        }

        // Setup button listener
        setupButtonListener()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        fetchUserIfAuthenticated()
    }

    private fun fetchUserIfAuthenticated() {
        // Check authentication status from session manager
        if (sessionManager.getAuthStatus() == REGISTERED_USER) {
            viewModel.fetchUser()
        } else {
            // Update ui state
            setUiState(isAuthenticated = false)
        }
    }

    private fun setUiState(isAuthenticated: Boolean) {
        // Visibility
        val visibility = if (isAuthenticated) View.VISIBLE else View.GONE

        // Menu header visibility
        binding.tvTembang.visibility = visibility

        // Menu visibility
        binding.menuSubmission.visibility = visibility
        binding.menuEditProfile.visibility = visibility
        binding.menuChangePassword.visibility = visibility
        binding.menuSignOut.visibility = visibility
        binding.menuSignIn.visibility = if (isAuthenticated) View.GONE else View.VISIBLE

        // Set profile name to Guest if isAuthenticated false
        binding.tvProfileName.text = getString(R.string.guest_name)

        // Button edit profile photo visibility
        binding.btnEditProfilePhoto.visibility = visibility
    }

    private fun setupButtonListener() {
        // Button edit profile photo
        binding.btnEditProfilePhoto.setOnClickListener {
            // TODO : Create photo chooser intent (camera & gallery) with crop 1:1 ratio
            Toast.makeText(activity, "Edit Profile Photo", Toast.LENGTH_SHORT).show()
        }

        // Menu my tembang
        binding.menuSubmission.setOnClickListener {
            // Navigate to user submission
            navigateToUserSubmission()
        }

        // Menu edit profile
        binding.menuEditProfile.setOnClickListener {
            // Navigate to EditProfileActivity
            Intent(activity, EditProfileActivity::class.java).also {
                startActivity(it)
            }
        }

        // Menu change password
        binding.menuChangePassword.setOnClickListener {
            // Navigate to ChangPasswordActivity
            Intent(activity, ChangePasswordActivity::class.java).also {
                startActivity(it)
            }
        }

        // Menu sign out
        binding.menuSignOut.setOnClickListener {
            // Do sign out
            viewModel.signOut()
        }

        // Menu sign in
        binding.menuSignIn.setOnClickListener {
            // Navigate to SignInActivity
            Intent(activity, SignInActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun userObserver(event: UserEvent) {
        when (event) {
            is UserEvent.Success -> {
                // Update ui state
                setUiState(isAuthenticated = true)

                // Set user information
                Glide.with(requireContext())
                    .load(event.user.photoUrl)
                    .placeholder(R.drawable.profile_avatar_placeholder)
                    .error(R.drawable.profile_avatar_placeholder)
                    .centerCrop()
                    .into(binding.ivProfileAvatar)
                binding.tvProfileName.text = event.user.name
            }
            is UserEvent.Error -> {
                Helpers.snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()

                // Update ui state
                setUiState(isAuthenticated = false)
            }
            is UserEvent.NetworkError -> {
                Helpers.snackBarNetworkError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ) {
                    viewModel.fetchUser()
                }.show()

                // Update ui state
                setUiState(isAuthenticated = false)
            }
            is UserEvent.Loading -> {}
        }
    }

    private fun signOutObserver(event: SignOutEvent) {
        when (event) {
            is SignOutEvent.Success -> {
                loadingDialog.dismiss()
                sessionManager.clearAuthentication()
                Intent(activity, SignInActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(it)
                }
            }
            is SignOutEvent.Error -> {
                loadingDialog.dismiss()
                Helpers.snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is SignOutEvent.NetworkError -> {
                loadingDialog.dismiss()
                Helpers.snackBarNetworkError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ) {
                    viewModel.fetchUser()
                }.show()
            }
            is SignOutEvent.Loading -> {
                if (!loadingDialog.isAdded) {
                    loadingDialog.show(
                        parentFragmentManager,
                        LoadingDialogFragment::class.java.simpleName
                    )
                }
            }
        }
    }

    private fun navigateToUserSubmission() {
        // Begin fragment transaction
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            .add(
                R.id.nav_host_fragment_container,
                UserSubmissionFragment::class.java,
                null
            )
            .addToBackStack(UserSubmissionFragment::class.java.simpleName)
            .setReorderingAllowed(true)
            .commit()
    }
}