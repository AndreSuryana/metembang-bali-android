package com.andresuryana.metembangbali.ui.add.additional

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.adapter.UsageAdapter
import com.andresuryana.metembangbali.adapter.dropdown.MoodStringAdapter
import com.andresuryana.metembangbali.adapter.dropdown.RuleStringAdapter
import com.andresuryana.metembangbali.adapter.viewpager.AddSubmissionViewPagerAdapter.Companion.MEDIA
import com.andresuryana.metembangbali.databinding.FragmentAdditionalBinding
import com.andresuryana.metembangbali.helper.Helpers
import com.andresuryana.metembangbali.ui.add.AddSubmissionViewModel
import com.andresuryana.metembangbali.ui.add.additional.rule.RuleBottomSheetDialog
import com.andresuryana.metembangbali.ui.add.additional.usage.UsageBottomSheetDialog
import com.andresuryana.metembangbali.utils.event.MoodEvent
import com.andresuryana.metembangbali.utils.event.RuleEvent
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
    private lateinit var moodAdapter: MoodStringAdapter
    private lateinit var ruleAdapter: RuleStringAdapter

    // Recycler adapter
    private val usageAdapter = UsageAdapter()

    // Bottom sheet dialog
    private val ruleDialog = RuleBottomSheetDialog()
    private val usageDialog = UsageBottomSheetDialog()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout
        _binding = FragmentAdditionalBinding.inflate(layoutInflater)

        // Setup dropdown input
        setupDropdown()

        // Setup usage recycler view
        binding.rvUsages.apply {
            isScrollContainer = false
            layoutManager = LinearLayoutManager(requireContext(), VERTICAL, false)
            adapter = usageAdapter
        }

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

        // Button add usage listener
        binding.btnAddUsage.setOnClickListener {
            usageDialog.show(parentFragmentManager, UsageBottomSheetDialog::class.java.simpleName)
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

        // Usage bottom sheet dialog
        usageDialog.setOnResultCallbackListener {
            // Add to usage list
            usageAdapter.addItem(it)
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
        val usages = usageAdapter.getAllItem()

        // Set additional data
        viewModel.meaning = meaning
        viewModel.lyricsIDN = lyricsIDN
        if (usages.isNotEmpty()) {
            viewModel.hasUsages.clear()
            viewModel.hasUsages.addAll(usages)
        }

        Log.d(
            "AdditionalFragment",
            "onNext: usage=${usages}, mood=${viewModel.mood}, rule=${viewModel.rule}, meaning=${viewModel.meaning}, lyricsIDN=${viewModel.lyricsIDN}"
        )

        // Set next page position
        viewModel.setPagePosition(MEDIA)
    }
}