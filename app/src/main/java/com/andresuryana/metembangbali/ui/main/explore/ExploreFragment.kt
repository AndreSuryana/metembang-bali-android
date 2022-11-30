package com.andresuryana.metembangbali.ui.main.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.adapter.CategoryAdapter
import com.andresuryana.metembangbali.data.model.Category
import com.andresuryana.metembangbali.databinding.FragmentExploreBinding
import com.andresuryana.metembangbali.dialog.LoadingDialogFragment
import com.andresuryana.metembangbali.helper.Helpers.snackBarError
import com.andresuryana.metembangbali.helper.Helpers.snackBarNetworkError
import com.andresuryana.metembangbali.ui.main.explore.result.ExploreResultFragment
import com.andresuryana.metembangbali.ui.main.explore.subcategory.SubCategoryFragment
import com.andresuryana.metembangbali.utils.CategoryConstants.SEKAR_AGUNG
import com.andresuryana.metembangbali.utils.event.CategoryEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreFragment : Fragment() {

    // Layout binding
    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    // View model
    private val viewModel: ExploreViewModel by viewModels()

    // Loading dialog
    private val loadingDialog = LoadingDialogFragment()

    // Recycler view adapter
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout
        _binding = FragmentExploreBinding.inflate(inflater, container, false)

        // Setup adapter
        categoryAdapter = CategoryAdapter()
        categoryAdapter.setOnItemClickListener(this::onCategoryClicked)

        // Setup recycler view
        setupRvCategory()

        // Get categories
        viewModel.getCategories()

        // Observe categories
        viewModel.categories.observe(viewLifecycleOwner, this::categoriesObserver)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRvCategory() {
        binding.rvCategory.apply {
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun onCategoryClicked(category: Category) {
        if (category.id == SEKAR_AGUNG) {
            // If category is SekarAgung (doesn't have any sub-categories),
            // then navigate to result fragment
            navigateToResultFragment(category.id)
        } else {
            // Otherwise, navigate to SubCategoryFragment
            navigateToSubCategoryFragment(category.id)
        }
    }

    private fun categoriesObserver(event: CategoryEvent) {
        when (event) {
            is CategoryEvent.Success -> {
                loadingDialog.dismiss()
                hideEmptyContainer()
                categoryAdapter.setList(event.categories)
                binding.rvCategory.adapter = categoryAdapter
            }
            is CategoryEvent.Error -> {
                loadingDialog.dismiss()
                showEmptyContainer(R.string.empty_category_not_found)
                snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is CategoryEvent.NetworkError -> {
                loadingDialog.dismiss()
                showEmptyContainer(R.string.empty_category_not_found)
                snackBarNetworkError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ) {
                    viewModel.getCategories()
                }.show()
            }
            is CategoryEvent.Loading -> {
                if (!loadingDialog.isAdded) {
                    loadingDialog.show(
                        parentFragmentManager,
                        LoadingDialogFragment::class.java.simpleName
                    )
                }
            }
            is CategoryEvent.Empty -> {
                loadingDialog.dismiss()
                showEmptyContainer(R.string.empty_category_not_found)
            }
        }
    }

    private fun navigateToSubCategoryFragment(categoryId: String) {
        // Init bundle arguments
        val bundle = Bundle()
        bundle.putString(SubCategoryFragment.EXTRA_CATEGORY_ID, categoryId)

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
                SubCategoryFragment::class.java,
                bundle
            )
            .addToBackStack(SubCategoryFragment::class.java.simpleName)
            .setReorderingAllowed(true)
            .commit()
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
}