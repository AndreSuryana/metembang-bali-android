package com.andresuryana.metembangbali.ui.add.additional

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.adapter.dropdown.MoodStringAdapter
import com.andresuryana.metembangbali.adapter.dropdown.RuleStringAdapter
import com.andresuryana.metembangbali.adapter.dropdown.UsageStringAdapter
import com.andresuryana.metembangbali.adapter.dropdown.UsageTypeStringAdapter
import com.andresuryana.metembangbali.adapter.viewpager.AddSubmissionViewPagerAdapter.Companion.MEDIA
import com.andresuryana.metembangbali.databinding.FragmentAdditionalBinding
import com.andresuryana.metembangbali.helper.Helpers
import com.andresuryana.metembangbali.ui.add.AddSubmissionViewModel
import com.andresuryana.metembangbali.ui.add.additional.rule.RuleBottomSheetDialog
import com.andresuryana.metembangbali.utils.event.MoodEvent
import com.andresuryana.metembangbali.utils.event.RuleEvent
import com.andresuryana.metembangbali.utils.event.UsageEvent
import com.andresuryana.metembangbali.utils.event.UsageTypeEvent
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdditionalFragment : Fragment() {

    // Layout binding
    private var _binding: FragmentAdditionalBinding? = null
    private val binding get() = _binding!!

    // View model
    private val viewModel: AddSubmissionViewModel by viewModels(ownerProducer = {
        requireActivity()
    })

    // Array adapter
    private lateinit var usageTypeAdapter: UsageTypeStringAdapter
    private lateinit var usageAdapter: UsageStringAdapter
    private lateinit var moodAdapter: MoodStringAdapter
    private lateinit var ruleAdapter: RuleStringAdapter

    // Bottom sheet dialog
    private val ruleDialog = RuleBottomSheetDialog()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout
        _binding = FragmentAdditionalBinding.inflate(layoutInflater)

        // Setup dropdown input
        setupDropdown()

        // Init lyrics input
        if (binding.lyricsInputContainer.childCount <= 0) {
            addLyricsInput()
        }

        // Get dropdown values
        viewModel.getUsageTypes()
        viewModel.getMoods()
        viewModel.getRules()

        // Setup button listener
        setupButtonListener()

        // Setup bottom sheet dialog
        setupBottomSheetDialog()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // Observe usage types
        viewModel.usageTypes.observe(viewLifecycleOwner, this::usageTypesObserver)

        // Observe usages
        viewModel.usages.observe(viewLifecycleOwner, this::usagesObserver)

        // Observe moods
        viewModel.moods.observe(viewLifecycleOwner, this::moodsObserver)

        // Observe rules
        viewModel.rules.observe(viewLifecycleOwner, this::rulesObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupDropdown() {
        // Usage type dropdown listener
        binding.acUsageType.setOnItemClickListener { _, _, position, _ ->
            viewModel.usageType = usageTypeAdapter.getItem(position)
            viewModel.getUsages(viewModel.usageType?.id)
            viewModel.hasUsages?.clear() /* Temporary */
            binding.acUsage.setText("")
        }

        // Usage dropdown listener
        binding.acUsage.setOnItemClickListener { _, _, position, _ ->
            viewModel.hasUsages?.add(0, usageAdapter.getItem(position))
        }

        // Sub category dropdown listener
        binding.acMood.setOnItemClickListener { _, _, position, _ ->
            viewModel.mood = moodAdapter.getItem(position)
        }

        // Sub category dropdown listener
        binding.acRule.setOnItemClickListener { _, _, position, _ ->
            viewModel.rule = ruleAdapter.getItem(position)
        }
    }

    private fun setupButtonListener() {
        // Button prev listener
        binding.btnPrev.setOnClickListener {
            activity?.onBackPressed()
        }

        // Button next listener
        binding.btnNext.setOnClickListener {
            onNext()
        }

        // Button add rule listener
        binding.btnAddRule.setOnClickListener {
            ruleDialog.show(parentFragmentManager, RuleBottomSheetDialog::class.java.simpleName)
        }
    }

    private fun setupBottomSheetDialog() {
        // Rule bottom sheet dialog
        ruleDialog.setOnResultCallbackListener {
            // Add to current rule dropdown
            ruleAdapter.add(it)

            // Set selected dropdown
            binding.acRule.setText(it.toString())

            // Set current rule
            viewModel.rule = it
        }
    }

    private fun addLyricsInput() {
        // Inflate layout input item
        val index = binding.lyricsInputContainer.childCount
        val child = LayoutInflater.from(activity)
            .inflate(R.layout.item_lyrics_input_item, null) as TextInputLayout
        child.editText?.hint = getString(R.string.hint_lyrics_input, index + 1)

        // Remove focus listener on previous child view
        if (index > 0) {
            val prevChild = binding.lyricsInputContainer.getChildAt(index - 1) as TextInputLayout
            prevChild.editText?.onFocusChangeListener = null
        }

        // Add focus listener on child view
        child.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                addLyricsInput()
            }
        }

        binding.lyricsInputContainer.addView(child, index)
    }

    private fun getLyricsList(): ArrayList<String> {
        // Init list
        val lyrics = ArrayList<String>()

        // Iterate through the child
        for (i in 0 until binding.lyricsInputContainer.childCount) {
            // Get child view
            val child = binding.lyricsInputContainer.getChildAt(i) as TextInputLayout

            // Add lyrics item to the list
            val lyricsText = child.editText?.text?.trim().toString()
            if (lyricsText.isNotBlank()) lyrics.add(lyricsText)
        }

        return lyrics
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
                setUsageVisibility(View.VISIBLE)
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
            is UsageEvent.Empty -> {
                setUsageVisibility(View.GONE)
            }
        }
    }

    private fun moodsObserver(event: MoodEvent) {
        when (event) {
            is MoodEvent.Success -> {
                moodAdapter = MoodStringAdapter(
                    requireContext(),
                    R.layout.item_dropdown,
                    event.moods
                )
                binding.acMood.setAdapter(moodAdapter)
            }
            is MoodEvent.Error -> {
                Helpers.snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is MoodEvent.NetworkError -> {
                Helpers.snackBarError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            is MoodEvent.Loading -> {}
            is MoodEvent.Empty -> {}
        }
    }

    private fun rulesObserver(event: RuleEvent) {
        when (event) {
            is RuleEvent.Success -> {
                ruleAdapter = RuleStringAdapter(
                    requireContext(),
                    R.layout.item_dropdown,
                    event.rules
                )
                binding.acRule.setAdapter(ruleAdapter)
            }
            is RuleEvent.Error -> {
                Helpers.snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is RuleEvent.NetworkError -> {
                Helpers.snackBarError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            is RuleEvent.Loading -> {}
            is RuleEvent.Empty -> {}
        }
    }

    private fun onNext() {
        // Get value
        val meaning = binding.etMeaning.text?.trim().toString()
        val lyricsIDN = getLyricsList()

        // Reset helper text
        binding.apply {
            tilUsage.helperText = ""
        }

        // Validation
        if (binding.tilUsage.isVisible && viewModel.hasUsages?.isEmpty() == true) {
            binding.tilUsage.apply {
                helperText = getString(R.string.helper_empty_usage)
                requestFocus()
            }
            return
        }

        Log.d(
            "AdditionalFragment",
            "onNext: usageType=${viewModel.usageType}, usage=${viewModel.hasUsages}, mood=${viewModel.mood}, rule=${viewModel.rule}, meaning=$meaning, lyricsIDN=$lyricsIDN"
        )

        // Set additional data
        viewModel.meaning = meaning
        viewModel.lyricsIDN = lyricsIDN

        // Set next page position
        viewModel.setPagePosition(MEDIA)
    }

    private fun setUsageVisibility(visibility: Int) {
        binding.labelUsage.visibility = visibility
        binding.tilUsage.visibility = visibility
    }
}