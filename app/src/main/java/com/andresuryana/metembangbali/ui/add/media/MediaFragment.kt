package com.andresuryana.metembangbali.ui.add.media

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.databinding.FragmentMediaBinding
import com.andresuryana.metembangbali.helper.AnimationHelper.animateSlide
import com.andresuryana.metembangbali.helper.Helpers
import com.andresuryana.metembangbali.helper.Helpers.snackBarWarning
import com.andresuryana.metembangbali.helper.IntentHelper
import com.andresuryana.metembangbali.ui.add.AddSubmissionViewModel
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.ImagePicker.Companion.RESULT_ERROR
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class MediaFragment : Fragment() {

    // Layout binding
    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!

    // View model
    private val viewModel: AddSubmissionViewModel by viewModels(ownerProducer = {
        requireActivity()
    })

    // Image picker register activity result
    private val startImagePickerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            lifecycleScope.launch(Dispatchers.IO) {
                when (it.resultCode) {
                    RESULT_OK -> {
                        // Image uri from data
                        val imageUri = it.data?.data!!

                        // Convert uri to file
                        viewModel.coverImageFile = imageUri.toFile()

                        // Image view
                        withContext(Dispatchers.Main) {
                            Glide.with(binding.root)
                                .load(imageUri)
                                .placeholder(R.drawable.ic_cover_placeholder)
                                .error(R.drawable.ic_cover_placeholder)
                                .into(binding.ivCover)
                        }
                    }
                    RESULT_ERROR -> {
                        Helpers.snackBarError(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout
        _binding = FragmentMediaBinding.inflate(layoutInflater)

        // Setup radio button listener
        setupRadioButtonListener()

        // Setup button listener
        setupButtonListener()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupRadioButtonListener() {
        // Radio group cover
        binding.rgCover.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_no_cover -> {
                    // Hide add cover container
                    binding.addCoverContainer.animateSlide(
                        currentHeight = binding.addCoverContainer.measuredHeight,
                        newHeight = 0
                    )
                }
                R.id.rb_add_cover -> {
                    // Set measured params
                    binding.addCoverContainer.measure(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )

                    // Show add cover container
                    binding.addCoverContainer.animateSlide(
                        currentHeight = 0,
                        newHeight = binding.addCoverContainer.measuredHeight
                    )
                }
            }
        }

        // Radio group audio
        binding.rgAudio.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_no_audio -> {
                    // Hide audio file container
                    binding.audioFileContainer.animateSlide(
                        currentHeight = binding.audioFileContainer.measuredHeight,
                        newHeight = 0
                    )

                    // TODO : Hide audio recorder container
                }
                R.id.rb_audio_file -> {
                    // Set measured params
                    binding.audioFileContainer.measure(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )

                    // Show audio file container
                    binding.audioFileContainer.animateSlide(
                        currentHeight = 0,
                        newHeight = binding.audioFileContainer.measuredHeight
                    )
                }
                R.id.rb_record_audio -> {
                    // TODO : Show audio recorder container
                    Toast.makeText(activity, "Record audio", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupButtonListener() {
        // Button upload image listener
        binding.btnUploadImage.setOnClickListener {
            startImagePickerResult.launch(IntentHelper(requireActivity()).chooserImage)
        }

        // Button upload audio listener
        binding.btnUploadAudio.setOnClickListener {
            // TODO : Create activity result intent for pick audio file
        }

        // Button prev listener
        binding.btnPrev.setOnClickListener {
            activity?.onBackPressed()
        }

        // Button next listener
        binding.btnFinish.setOnClickListener {
            onFinish()
        }
    }

    private fun onFinish() {
        // Get value
        val coverSource = binding.etCoverSource.text?.trim().toString()

        // Reset helper text
        binding.tilAudioFile.helperText = ""

        // Validation
        if (binding.addCoverContainer.isVisible) {
            if (viewModel.coverImageFile == null) {
                snackBarWarning(
                    binding.root,
                    getString(R.string.warning_no_image_file),
                    Snackbar.LENGTH_SHORT
                ).show()
                binding.ivCover.requestFocus()
                return
            }
        }

        if (binding.audioFileContainer.isVisible) {
            if (viewModel.audioFile == null) {
                binding.tilAudioFile.apply {
                    helperText = getString(R.string.helper_empty_audio_file)
                    requestFocus()
                }
                return
            }
        }

        // TODO : Validate audio recorder


        // Set media data
        viewModel.coverSource = coverSource.ifBlank { null }

        // Show prompt alert
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.title_add_submission)
            .setMessage(R.string.prompt_add_submission)
            .setCancelable(true)
            .setPositiveButton(R.string.answer_yes) { _, _ ->
                viewModel.createSubmission()
            }
            .setNegativeButton(R.string.answer_no) { _, _ ->
                // Do nothing
            }
            .setNeutralButton(R.string.answer_cancel) { _, _ ->
                // Do nothing
            }.show()
    }
}