package com.andresuryana.metembangbali.ui.main.explore.subcategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.adapter.SubCategoryAdapter
import com.andresuryana.metembangbali.data.model.SubCategory
import com.andresuryana.metembangbali.databinding.FragmentExploreBinding
import com.andresuryana.metembangbali.helper.Helpers.snackBarError
import com.andresuryana.metembangbali.helper.Helpers.snackBarNetworkError
import com.andresuryana.metembangbali.ui.main.explore.result.ExploreResultFragment
import com.andresuryana.metembangbali.utils.Ext.spaceCamelCase
import com.andresuryana.metembangbali.utils.event.SubCategoryEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubCategoryFragment : Fragment() {

    // Layout binding
    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    // View model
    private val viewModel: SubCategoryViewModel by viewModels()

    // Recycler view adapter
    private val subCategoryAdapter = SubCategoryAdapter()

    // Parent category name
    private var categoryId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout
        _binding = FragmentExploreBinding.inflate(inflater, container, false)

        // Show back button
        binding.btnBack.visibility = View.VISIBLE

        // Get category name from arguments
        categoryId = arguments?.getString(EXTRA_CATEGORY_ID)

        // Set title
        binding.tvTitle.text = categoryId?.spaceCamelCase()

        // Setup adapter
        subCategoryAdapter.setOnItemClickListener(this::onSubCategoryClicked)

        // Setup recycler view
        setupRvSubCategory()

        // Get categories
        viewModel.getSubCategories(categoryId)

        // Observe categories
        viewModel.subCategories.observe(viewLifecycleOwner, this::subCategoriesObserver)

        // Setup button listener
        setupButtonListener()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRvSubCategory() {
        binding.rvCategory.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setupButtonListener() {
        // Button back listener
        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun onSubCategoryClicked(subCategory: SubCategory) {
        // Navigate to result fragment
        navigateToResultFragment(subCategory.id)
    }

    private fun subCategoriesObserver(event: SubCategoryEvent) {
        when (event) {
            is SubCategoryEvent.Success -> {
                hideEmptyContainer()
                subCategoryAdapter.setList(event.subCategories)
                binding.rvCategory.adapter = subCategoryAdapter
            }
            is SubCategoryEvent.Error -> {
                showEmptyContainer(R.string.empty_category_not_found)
                snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is SubCategoryEvent.NetworkError -> {
                showEmptyContainer(R.string.empty_category_not_found)
                snackBarNetworkError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ) {
                    viewModel.getSubCategories(categoryId)
                }.show()
            }
            is SubCategoryEvent.Loading -> {}
            is SubCategoryEvent.Empty -> {
                showEmptyContainer(R.string.empty_category_not_found)
            }
        }
    }

    private fun navigateToResultFragment(categoryId: String) {
        // Init bundle arguments
        val bundle = Bundle()
        bundle.putString(ExploreResultFragment.EXTRA_CATEGORY_ID, categoryId)

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
                ExploreResultFragment::class.java,
                bundle
            )
            .addToBackStack(ExploreResultFragment::class.java.simpleName)
            .setReorderingAllowed(true)
            .commit()
    }

    private fun showEmptyContainer(stringRes: Int?) {
        if (stringRes != null) {
            binding.emptyContainer.tvEmptyTitle.setText(stringRes)
        }
        binding.emptyContainer.root.visibility = View.VISIBLE
        binding.rvCategory.visibility = View.GONE
    }

    private fun hideEmptyContainer() {
        binding.emptyContainer.root.visibility = View.GONE
        binding.rvCategory.visibility = View.VISIBLE
    }

    companion object {
        const val EXTRA_CATEGORY_ID = "category_id"
    }
}