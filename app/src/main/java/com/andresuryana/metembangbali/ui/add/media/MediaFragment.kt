package com.andresuryana.metembangbali.ui.add.media

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.databinding.FragmentMediaBinding
import com.andresuryana.metembangbali.dialog.LoadingDialogFragment
import com.andresuryana.metembangbali.helper.AnimationHelper.animateSlide
import com.andresuryana.metembangbali.helper.Helpers
import com.andresuryana.metembangbali.helper.Helpers.snackBarWarning
import com.andresuryana.metembangbali.ui.add.AddSubmissionViewModel
import com.andresuryana.metembangbali.utils.AudioRecorder
import com.andresuryana.metembangbali.utils.event.SubmissionEvent
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.ImagePicker.Companion.RESULT_ERROR
import com.github.squti.androidwaverecorder.RecorderState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.concurrent.schedule

@AndroidEntryPoint
class MediaFragment : Fragment() {

    // Layout binding
    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!

    // View model
    private val viewModel: AddSubmissionViewModel by viewModels(ownerProducer = {
        requireActivity()
    })

    // Loading dialog
    private val loadingDialog = LoadingDialogFragment()

    // Audio recorder
    private lateinit var recorder: AudioRecorder

    // Recorder state
    private var isRecording: Boolean = false

    // Image picker register activity result
    private val startImagePickerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            lifecycleScope.launch(Dispatchers.IO) {
                when (it.resultCode) {
                    RESULT_OK -> {
                        // Image uri from data
                        val imageUri = it.data?.data!!
                        Log.d("MediaFragment", "imageResult: uri=$imageUri")

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

//    // Audio register activity result
//    private val startAudioForResult =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            try {
//                if (it.resultCode == RESULT_OK && it.data?.data != null) {
//                    // Audio uri from data
//                    val audioUri = it.data?.data!!
//                    Log.d("MediaFragment", "audioResult: uri=$audioUri}")
//
//                    // Convert uri to file
//                    val audioFile = audioUri.toAudioFile(requireActivity())
//                    Log.d("MediaFragment", "audioResultFile: uri=${audioFile?.absoluteFile}")
//                    Log.d("MediaFragment", "audioResultFile: name=${audioFile?.name}")
//                    Log.d("MediaFragment", "audioResultFile: extension=${audioFile?.extension}")
//
//                    viewModel.audioFile = audioFile
//
//                    // Display path name
//                    binding.etAudioFile.setText(audioFile?.name)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout
        _binding = FragmentMediaBinding.inflate(layoutInflater)

        // Init audio recorder
        recorder = AudioRecorder(requireContext())
        recorder.setOnAmplitudeListener {
            Log.d("AudioRecorder", "onAmplitudeListener: amplitude=$it")
            binding.recordView.update(it)
        }
        recorder.setOnStateChangeListener {
            when (it) {
                RecorderState.RECORDING -> recorderStateStart()
                RecorderState.STOP -> recorderStateStop()
                else -> recorderStateStop()
            }
        }

        // Setup radio button listener
        setupRadioButtonListener()

        // Observe submission
        lifecycleScope.launchWhenStarted {
            viewModel.submission.collectLatest { observeSubmission(it) }
        }

        // Setup button listener
        setupButtonListener()

        // TODO : Remove this code if audio file picker already fixed!!!
        binding.rbAudioFile.visibility = View.GONE

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

                    // Reset value
                    viewModel.coverImageFile = null
                    viewModel.coverSource = null
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

                    // Hide audio record container
                    binding.audioRecordContainer.animateSlide(
                        currentHeight = binding.audioRecordContainer.measuredHeight,
                        newHeight = 0
                    )

                    // Reset value
                    viewModel.audioFile = null
                }
//                R.id.rb_audio_file -> {
//                    // Hide audio record container
//                    binding.audioRecordContainer.animateSlide(
//                        currentHeight = binding.audioRecordContainer.measuredHeight,
//                        newHeight = 0
//                    )
//
//                    // Set measured params
//                    binding.audioFileContainer.measure(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT
//                    )
//
//                    // Show audio file container
//                    binding.audioFileContainer.animateSlide(
//                        currentHeight = 0,
//                        newHeight = binding.audioFileContainer.measuredHeight
//                    )
//
//                    // Clear file path in edit text
//                    binding.etAudioFile.setText("")
//
//                    // Reset value
//                    viewModel.audioFile = null
//
//                }
                R.id.rb_record_audio -> {
                    // Reset value
                    viewModel.audioFile = null

                    // Hide audio file container
                    binding.audioFileContainer.animateSlide(
                        currentHeight = binding.audioFileContainer.measuredHeight,
                        newHeight = 0
                    )

                    // Set measured params
                    binding.audioRecordContainer.measure(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )

                    // Show audio record container
                    binding.audioRecordContainer.animateSlide(
                        currentHeight = 0,
                        newHeight = binding.audioRecordContainer.measuredHeight
                    )
                }
            }
        }
    }

    private fun setupButtonListener() {
        // Button upload image listener
        binding.btnUploadImage.setOnClickListener {
            ImagePicker.with(requireActivity())
                .cropSquare()
                .galleryMimeTypes(arrayOf("image/jpg", "image/jpeg", "image/png"))
                .maxResultSize(400, 400)
                .compress(1024)
                .saveDir(File(activity?.filesDir, "Metembang Bali"))
                .createIntent {
                    startImagePickerResult.launch(it)
                }
        }

//        // Button upload audio listener
//        binding.btnUploadAudio.setOnClickListener {
//            Intent().also {
//                it.action = Intent.ACTION_GET_CONTENT
//                it.type = "audio/*"
//                it.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("audio/mpeg", "audio/wav"))
//                startAudioForResult.launch(it)
//            }
//        }

        // Button record/stop listener
        binding.btnRecord.setOnClickListener {
            // Start/stop recorder
            if (!isRecording) {
                // Check permission
                if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        70
                    )
                } else {
                    recorder.startRecording()
                }
            } else {
                recorder.stopRecordingForResult {
                    viewModel.audioFile = it
                }
            }
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
        binding.tilCoverSource.helperText = ""
        binding.tilAudioFile.helperText = ""

        // Validation
        if (binding.rbAddCover.isSelected) {
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

        if (binding.rbAudioFile.isSelected || binding.rbRecordAudio.isSelected) {
            if (viewModel.audioFile == null) {
                binding.tilAudioFile.apply {
                    helperText = getString(R.string.helper_empty_audio_file)
                    requestFocus()
                }
                return
            }
        }

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
                activity?.finish()
            }
            .setNeutralButton(R.string.answer_cancel) { _, _ ->
                // Do nothing
            }.show()
    }

    private fun observeSubmission(event: SubmissionEvent) {
        when (event) {
            is SubmissionEvent.Success -> {
                loadingDialog.dismiss()
                Helpers.snackBarSuccess(
                    binding.root,
                    getString(R.string.success_add_submission),
                    Snackbar.LENGTH_SHORT
                ).show()
                Timer().schedule(1000L) {
                    activity?.finish()
                }
            }
            is SubmissionEvent.Error -> {
                loadingDialog.dismiss()
                Helpers.snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is SubmissionEvent.NetworkError -> {
                loadingDialog.dismiss()
                Helpers.snackBarError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            is SubmissionEvent.Loading -> {
                if (!loadingDialog.isAdded) {
                    loadingDialog.show(
                        parentFragmentManager,
                        LoadingDialogFragment::class.java.simpleName
                    )
                }
            }
        }
    }

    private fun recorderStateStart() {
        // Update recorder state
        isRecording = true

        // Update button state
        binding.btnRecord.setImageResource(R.drawable.ic_stop)
    }

    private fun recorderStateStop() {
        // Update recorder state
        isRecording = false

        // Update button state
        binding.btnRecord.setImageResource(R.drawable.ic_record)
    }
}