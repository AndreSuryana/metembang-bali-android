package com.andresuryana.metembangbali.ui.add.additional.usage

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.adapter.dropdown.UsageStringAdapter
import com.andresuryana.metembangbali.adapter.dropdown.UsageTypeStringAdapter
import com.andresuryana.metembangbali.data.model.Usage
import com.andresuryana.metembangbali.data.model.UsageType
import com.andresuryana.metembangbali.databinding.FragmentUsageBottomSheetDialogBinding
import com.andresuryana.metembangbali.helper.Helpers
import com.andresuryana.metembangbali.ui.add.AddSubmissionViewModel
import com.andresuryana.metembangbali.utils.Ext.capitalizeEachWord
import com.andresuryana.metembangbali.utils.event.UsageEvent
import com.andresuryana.metembangbali.utils.event.UsageTypeEvent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UsageBottomSheetDialog : BottomSheetDialogFragment() {

    // Layout binding
    private var _binding: FragmentUsageBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

    // View model
    private val viewModel: AddSubmissionViewModel by viewModels(ownerProducer = {
        requireActivity()
    })

    // Array adapter
    private lateinit var usageTypeAdapter: UsageTypeStringAdapter
    private lateinit var usageAdapter: UsageStringAdapter

    // Current values
    private var usageType: UsageType? = null
    private var usage: Usage? = null

    // Callback
    private var onResultCallback: ((usage: Usage) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout
        _binding = FragmentUsageBottomSheetDialogBinding.inflate(layoutInflater)

        // Button set filter color
        binding.btnAddUsage.apply {
            setTextColor(ColorStateList.valueOf(Color.WHITE))
            backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.color_primary)
            )
        }

        // Setup dropdown input
        setupDropdown()

        // Button add rule listener
        binding.btnAddUsage.setOnClickListener {
            onAddUsage()
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        clearInput()
    }

    override fun onResume() {
        super.onResume()

        // Observe usage types
        viewModel.usageTypes.observe(viewLifecycleOwner, this::usageTypesObserver)

        // Observe usages
        viewModel.usages.observe(viewLifecycleOwner, this::usagesObserver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup sheet behaviour
        BottomSheetBehavior.from(view.parent as View)
            .state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun setOnResultCallbackListener(onResultCallback: (usage: Usage) -> Unit) {
        this.onResultCallback = onResultCallback
    }

    private fun setupDropdown() {
        // Usage type dropdown listener
        binding.acUsageType.setOnItemClickListener { _, _, position, _ ->
            usageType = usageTypeAdapter.getItem(position)
            viewModel.getUsages(usageType?.id)
            binding.acUsage.setText("")
            usage = null
        }

        // Usage dropdown listener
        binding.acUsage.setOnItemClickListener { _, _, position, _ ->
            usage = usageAdapter.getItem(position)
        }
    }

    private fun usageTypesObserver(event: UsageTypeEvent) {
        when (event) {
            is UsageTypeEvent.Success -> {
                usageTypeAdapter = UsageTypeStringAdapter(
                    requireContext(),
                    R.layout.item_dropdown,
                    event.usageTypes
                )
                binding.acUsageType.setAdapter(usageTypeAdapter)
            }
            is UsageTypeEvent.Error -> {
                Helpers.snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is UsageTypeEvent.NetworkError -> {
                Helpers.snackBarError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            is UsageTypeEvent.Loading -> {}
            is UsageTypeEvent.Empty -> {}
        }
    }

    private fun usagesObserver(event: UsageEvent) {
        when (event) {
            is UsageEvent.Success -> {
                usageAdapter = UsageStringAdapter(
                    requireContext(),
                    R.layout.item_dropdown,
                    event.usages
                )
                binding.acUsage.setAdapter(usageAdapter)
            }
            is UsageEvent.Error -> {
                Helpers.snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is UsageEvent.NetworkError -> {
                Helpers.snackBarError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            is UsageEvent.Loading -> {}
            is UsageEvent.Empty -> {}
        }
    }

    private fun onAddUsage() {
        // Get value
        val usageActivity = binding.acUsage.text.trim().toString().capitalizeEachWord()

        // Reset helper text
        binding.apply {
            tilUsageType.helperText = ""
            tilUsage.helperText = ""
        }

        // Validation
        if (usageType == null) {
            binding.tilUsageType.apply {
                helperText = getString(R.string.helper_empty_usage_type)
                requestFocus()
            }
            return
        }

        if (usageActivity?.isBlank() == true) {
            binding.tilUsage.apply {
                helperText = getString(R.string.helper_empty_usage)
                requestFocus()
            }
            return
        }

        // Return current usage
        onResultCallback?.invoke(Usage(null, usageType?.id, usageActivity))

        clearInput()
    }

    private fun clearInput() {
        // Clear input
        binding.apply {
            acUsageType.setText("")
            acUsage.setText("")
        }
    }
}