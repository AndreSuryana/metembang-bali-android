package com.andresuryana.metembangbali.ui.add.general

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.adapter.dropdown.CategoryStringAdapter
import com.andresuryana.metembangbali.adapter.dropdown.SubCategoryStringAdapter
import com.andresuryana.metembangbali.adapter.viewpager.AddSubmissionViewPagerAdapter.Companion.ADDITIONAL
import com.andresuryana.metembangbali.databinding.FragmentGeneralBinding
import com.andresuryana.metembangbali.helper.Helpers
import com.andresuryana.metembangbali.helper.Helpers.snackBarWarning
import com.andresuryana.metembangbali.ui.add.AddSubmissionViewModel
import com.andresuryana.metembangbali.utils.CategoryConstants
import com.andresuryana.metembangbali.utils.event.CategoryEvent
import com.andresuryana.metembangbali.utils.event.SubCategoryEvent
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GeneralFragment : Fragment() {

    // Layout binding
    private var _binding: FragmentGeneralBinding? = null
    private val binding get() = _binding!!

    // View model
    private val viewModel: AddSubmissionViewModel by viewModels(ownerProducer = {
        requireActivity()
    })

    // Array adapter
    private lateinit var categoryAdapter: CategoryStringAdapter
    private lateinit var subCategoryAdapter: SubCategoryStringAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout
        _binding = FragmentGeneralBinding.inflate(layoutInflater)

        // Setup dropdown input
        setupDropdown()

        // Init lyrics input
        if (binding.lyricsInputContainer.childCount <= 0) {
            addLyricsInput()
        }

        // Observe categories
        viewModel.categories.observe(viewLifecycleOwner, this::categoriesObserver)

        // Observer sub-categories
        viewModel.subCategories.observe(viewLifecycleOwner, this::subCategoriesObserver)

        // Setup button listener
        setupButtonListener()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        viewModel.getCategories()
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

    private fun onNext() {
        // Get value
        val title = binding.etTitle.text?.trim().toString()
        val lyrics = getLyricsList()

        // Reset helper text
        binding.apply {
            tilTitle.helperText = ""
            tilCategory.helperText = ""
            tilSubCategory.helperText = ""
        }

        // Validation
        if (title.isBlank()) {
            binding.tilTitle.apply {
                helperText = getString(R.string.helper_empty_title)
                requestFocus()
            }
            return
        }

        if (viewModel.category == null) {
            binding.tilCategory.apply {
                helperText = getString(R.string.helper_empty_category)
                requestFocus()
            }
            return
        }

        if (binding.tilSubCategory.isVisible && viewModel.subCategory == null) {
            binding.tilSubCategory.apply {
                helperText = getString(R.string.helper_empty_sub_category)
                requestFocus()
            }
            return
        }

        lyrics.ifEmpty {
            binding.lyricsInputContainer.requestFocus()
            snackBarWarning(binding.root, getString(R.string.helper_empty_lyrics)).show()
            return
        }

        Log.d(
            "GeneralFragment",
            "onNext: title=$title, category=${viewModel.category}, subCategory=${viewModel.subCategory}, lyrics=$lyrics"
        )

        // Set general data
        viewModel.title = title
        viewModel.lyrics = lyrics


        // Set next page position
        viewModel.setPagePosition(ADDITIONAL)
    }

    private fun setSubCategoryVisibility(visibility: Int) {
        binding.labelSubCategory.visibility = visibility
        binding.tilSubCategory.visibility = visibility
    }
}