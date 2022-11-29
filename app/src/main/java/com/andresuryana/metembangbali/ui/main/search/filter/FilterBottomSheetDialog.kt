package com.andresuryana.metembangbali.ui.main.search.filter

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.adapter.dropdown.*
import com.andresuryana.metembangbali.data.model.*
import com.andresuryana.metembangbali.databinding.FragmentFilterBottomSheetDialogBinding
import com.andresuryana.metembangbali.helper.Helpers
import com.andresuryana.metembangbali.utils.CategoryConstants
import com.andresuryana.metembangbali.utils.event.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterBottomSheetDialog : BottomSheetDialogFragment() {

    // Layout binding
    private var _binding: FragmentFilterBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

    // View model
    private val viewModel: FilterViewModel by viewModels(ownerProducer = {
        requireActivity()
    })

    // Callback
    private var onResultCallback: ((filter: SearchFilter) -> Unit)? = null

    // Array adapter
    private lateinit var categoryAdapter: CategoryStringAdapter
    private lateinit var subCategoryAdapter: SubCategoryStringAdapter
    private lateinit var usageTypeAdapter: UsageTypeStringAdapter
    private lateinit var usageAdapter: UsageStringAdapter
    private lateinit var moodAdapter: MoodStringAdapter
    private lateinit var ruleAdapter: RuleStringAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout
        _binding = FragmentFilterBottomSheetDialogBinding.inflate(inflater, container, false)

        // Setup dropdown input
        setupDropdown()

        // Get filters
        viewModel.getCategories()
        viewModel.getUsageTypes()
        viewModel.getMoods()
        viewModel.getRules()

        // Button set filter color
        binding.btnSetFilter.apply {
            setTextColor(ColorStateList.valueOf(Color.WHITE))
            backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.color_primary)
            )
        }

        // Setup button listener
        setupButtonListener()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup sheet behaviour to expanded
        BottomSheetBehavior.from(view.parent as View)
            .state = BottomSheetBehavior.STATE_EXPANDED

        // Set minimum height to parent view height
        binding.root.minHeight = Resources.getSystem().displayMetrics.heightPixels
    }

    override fun onResume() {
        super.onResume()

        // Setup observer
        setupObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupDropdown() {
        // Category dropdown listener
        binding.acCategory.setOnItemClickListener { _, _, position, _ ->
            viewModel.category = categoryAdapter.getItem(position)
            if (viewModel.category?.id == CategoryConstants.SEKAR_AGUNG) {
                // Hide sub-categories input
                setSubCategoryVisibility(View.GONE)
            } else {
                setSubCategoryVisibility(View.VISIBLE)
                viewModel.getSubCategories(viewModel.category?.id)
            }
            viewModel.subCategory = null
            binding.acSubCategory.setText("")
        }

        // Sub category dropdown listener
        binding.acSubCategory.setOnItemClickListener { _, _, position, _ ->
            viewModel.subCategory = subCategoryAdapter.getItem(position)
        }

        // Usage type dropdown listener
        binding.acUsageType.setOnItemClickListener { _, _, position, _ ->
            viewModel.usageType = usageTypeAdapter.getItem(position)
            viewModel.getUsages(viewModel.usageType?.id)
            viewModel.usage = null
            binding.acUsage.setText("")
        }

        // Usage dropdown listener
        binding.acUsage.setOnItemClickListener { _, _, position, _ ->
            viewModel.usage = usageAdapter.getItem(position)
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
        // Button set filter listener
        binding.btnSetFilter.setOnClickListener {
            // Check if there is no filter selected
            if (isFilterEmpty()) {
                Toast.makeText(context, getString(R.string.warning_no_search_filter), Toast.LENGTH_SHORT).show()
            } else {
                // Return current search filter
                onResultCallback?.invoke(
                    SearchFilter(
                        viewModel.category,
                        viewModel.subCategory,
                        viewModel.usageType,
                        viewModel.usage,
                        viewModel.mood,
                        viewModel.rule
                    )
                )
                dismiss()
            }
        }

        // Button top navigation listener
        binding.topNavigation.setOnClickListener {
            dismiss()
        }

        // Button reset filter listener
        binding.btnResetFilter.setOnClickListener {
            resetFilter()
            onResultCallback?.invoke(
                SearchFilter(null, null, null, null, null, null)
            )
        }
    }

    fun setOnResultCallbackListener(onResultCallback: (filter: SearchFilter) -> Unit) {
        this.onResultCallback = onResultCallback
    }

    private fun setupObserver() {
        // Observe categories
        viewModel.categories.observe(viewLifecycleOwner, this::categoriesObserver)

        // Observer sub-categories
        viewModel.subCategories.observe(viewLifecycleOwner, this::subCategoriesObserver)

        // Observe usage types
        viewModel.usageTypes.observe(viewLifecycleOwner, this::usageTypesObserver)

        // Observe usages
        viewModel.usages.observe(viewLifecycleOwner, this::usagesObserver)

        // Observe moods
        viewModel.moods.observe(viewLifecycleOwner, this::moodsObserver)

        // Observe rules
        viewModel.rules.observe(viewLifecycleOwner, this::rulesObserver)
    }

    private fun setSubCategoryVisibility(visibility: Int) {
        binding.labelSubCategory.visibility = visibility
        binding.tilSubCategory.visibility = visibility
    }

    private fun setUsageVisibility(visibility: Int) {
        binding.labelUsage.visibility = visibility
        binding.tilUsage.visibility = visibility
    }

    private fun isFilterEmpty(): Boolean = viewModel.category == null &&
            viewModel.subCategory == null &&
            viewModel.usageType == null &&
            viewModel.usage == null &&
            viewModel.mood == null &&
            viewModel.rule == null

    private fun resetFilter() {
        // Emptying all dropdown
        binding.acCategory.setText("")
        binding.acSubCategory.setText("")
        binding.acUsageType.setText("")
        binding.acUsage.setText("")
        binding.acMood.setText("")
        binding.acRule.setText("")

        // Reset visibility
        setSubCategoryVisibility(View.GONE)
        setUsageVisibility(View.GONE)

        // Reset variable
        viewModel.category = null
        viewModel.subCategory = null
        viewModel.usageType = null
        viewModel.usage = null
        viewModel.mood = null
        viewModel.rule = null
    }

    private fun categoriesObserver(event: CategoryEvent) {
        when (event) {
            is CategoryEvent.Success -> {
                categoryAdapter = CategoryStringAdapter(
                    requireContext(),
                    R.layout.item_dropdown,
                    event.categories
                )
                binding.acCategory.setAdapter(categoryAdapter)
            }
            is CategoryEvent.Error -> {
                Helpers.snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is CategoryEvent.NetworkError -> {
                Helpers.snackBarNetworkError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ) {
                    viewModel.getCategories()
                }.show()
            }
            is CategoryEvent.Loading -> {}
            is CategoryEvent.Empty -> {}
        }
    }

    private fun subCategoriesObserver(event: SubCategoryEvent) {
        when (event) {
            is SubCategoryEvent.Success -> {
                setSubCategoryVisibility(View.VISIBLE)
                subCategoryAdapter = SubCategoryStringAdapter(
                    requireContext(),
                    R.layout.item_dropdown,
                    event.subCategories
                )
                binding.acSubCategory.setAdapter(subCategoryAdapter)
            }
            is SubCategoryEvent.Error -> {
                Helpers.snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is SubCategoryEvent.NetworkError -> {
                Helpers.snackBarError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            is SubCategoryEvent.Loading -> {}
            is SubCategoryEvent.Empty -> {
                setSubCategoryVisibility(View.GONE)
            }
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
}