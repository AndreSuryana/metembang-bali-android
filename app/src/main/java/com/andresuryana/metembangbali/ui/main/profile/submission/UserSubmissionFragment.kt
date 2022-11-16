package com.andresuryana.metembangbali.ui.main.profile.submission

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.adapter.SubmissionAdapter
import com.andresuryana.metembangbali.data.model.Submission
import com.andresuryana.metembangbali.databinding.FragmentUserSubmissionBinding
import com.andresuryana.metembangbali.dialog.LoadingDialogFragment
import com.andresuryana.metembangbali.helper.Helpers
import com.andresuryana.metembangbali.helper.Helpers.checkErrorState
import com.andresuryana.metembangbali.ui.add.AddSubmissionActivity
import com.andresuryana.metembangbali.ui.main.detail.DetailActivity
import com.andresuryana.metembangbali.utils.SubmissionStatusConstants.STATUS_ACCEPTED
import com.andresuryana.metembangbali.utils.SubmissionStatusConstants.STATUS_PENDING
import com.andresuryana.metembangbali.utils.SubmissionStatusConstants.STATUS_REJECTED
import com.andresuryana.metembangbali.utils.event.DeleteSubmissionEvent
import com.andresuryana.metembangbali.utils.event.SubmissionListEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class UserSubmissionFragment : Fragment() {

    // Layout binding
    private var _binding: FragmentUserSubmissionBinding? = null
    private val binding get() = _binding!!

    // View model
    private val viewModel: UserSubmissionViewModel by viewModels()

    // Loading dialog
    private val loadingDialog = LoadingDialogFragment()

    // Recycler view adapter
    private val submissionAdapter = SubmissionAdapter()

    // Deleted submission item position
    private var deletedPosition: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout
        _binding = FragmentUserSubmissionBinding.inflate(layoutInflater)

        // Setup adapter
        submissionAdapter.setOnItemClickListener(this::onSubmissionClicked)
        submissionAdapter.setOnDeleteClickListener(this::onSubmissionDeleted)

        // Setup recycler view
        binding.rvSubmission.layoutManager = LinearLayoutManager(activity)

        // Get & observe user submissions
        viewModel.getUserSubmission()
        viewModel.submission.observe(viewLifecycleOwner, this::submissionObserver)

        // Observe delete submission
        lifecycleScope.launchWhenStarted {
            viewModel.deleteSubmission.collectLatest { deleteSubmissionObserver(it) }
        }

        // Setup button listener
        setupButtonListener()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupButtonListener() {
        // Button back listener
        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

        // Button add submission listener
        binding.fabAddSubmission.setOnClickListener {
            Intent(activity, AddSubmissionActivity::class.java).also {
                activity?.startActivity(it)
            }
        }
    }

    private fun onSubmissionClicked(submission: Submission) {
        when (submission.status) {
            STATUS_ACCEPTED -> {
                Intent(activity, DetailActivity::class.java).also {
                    it.putExtra(DetailActivity.EXTRA_TEMBANG_UID, submission.uid)
                    activity?.startActivity(it)
                }
            }
            STATUS_REJECTED -> {
                Helpers.snackBarError(
                    binding.root,
                    getString(R.string.warning_submission_status_rejected),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            STATUS_PENDING -> {
                Helpers.snackBarWarning(
                    binding.root,
                    getString(R.string.warning_submission_status_pending),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun onSubmissionDeleted(submission: Submission) {

        // Set current deleted submission item position
        deletedPosition = submissionAdapter.getItemPosition(submission)

        // Show alert
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.heading_delete_submission)
            .setMessage(R.string.prompt_delete_submission)
            .setCancelable(true)
            .setPositiveButton(R.string.answer_yes) { _, _ ->
                viewModel.deleteUserSubmission(submission.id)
            }
            .setNegativeButton(R.string.answer_no) { _, _ ->
                // Do nothing
            }
            .setNeutralButton(R.string.answer_cancel) { _, _ ->
                // Do nothing
            }.show()
    }

    private fun submissionObserver(event: SubmissionListEvent) {
        when (event) {
            is SubmissionListEvent.Success -> {
                loadingDialog.dismiss()
                submissionAdapter.setList(event.listResponse.list)
                binding.rvSubmission.adapter = submissionAdapter
            }
            is SubmissionListEvent.Error -> {
                loadingDialog.dismiss()
                checkErrorState(binding.root, event.message)
                Helpers.snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is SubmissionListEvent.NetworkError -> {
                loadingDialog.dismiss()
                Helpers.snackBarNetworkError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ) {
                    viewModel.getUserSubmission()
                }.show()

            }
            is SubmissionListEvent.Loading -> {
                if (!loadingDialog.isAdded) {
                    loadingDialog.show(
                        parentFragmentManager,
                        LoadingDialogFragment::class.java.simpleName
                    )
                }
            }
            is SubmissionListEvent.Empty -> {
                loadingDialog.dismiss()
                // TODO : Create empty layout
            }
        }
    }

    private fun deleteSubmissionObserver(event: DeleteSubmissionEvent) {
        when (event) {
            is DeleteSubmissionEvent.Success -> {
                loadingDialog.dismiss()
                deletedPosition?.let { submissionAdapter.removeItemAt(it) }
                Helpers.snackBarSuccess(
                    binding.root,
                    getString(R.string.success_delete_submission),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            is DeleteSubmissionEvent.Error -> {
                loadingDialog.dismiss()
                checkErrorState(binding.root, event.message)
                Helpers.snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is DeleteSubmissionEvent.NetworkError -> {
                loadingDialog.dismiss()
                Helpers.snackBarError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            is DeleteSubmissionEvent.Loading -> {
                if (!loadingDialog.isAdded) {
                    loadingDialog.show(
                        parentFragmentManager,
                        LoadingDialogFragment::class.java.simpleName
                    )
                }
            }
        }
    }
}