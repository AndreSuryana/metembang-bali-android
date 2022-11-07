package com.andresuryana.metembangbali.ui.main.search

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.adapter.ResultAdapter
import com.andresuryana.metembangbali.data.model.Tembang
import com.andresuryana.metembangbali.databinding.FragmentSearchBinding
import com.andresuryana.metembangbali.dialog.LoadingDialogFragment
import com.andresuryana.metembangbali.helper.Helpers
import com.andresuryana.metembangbali.ui.main.detail.DetailActivity
import com.andresuryana.metembangbali.ui.main.search.filter.FilterBottomSheetDialog
import com.andresuryana.metembangbali.utils.event.TembangListEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    // Layout binding
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    // View model
    private val viewModel: SearchViewModel by viewModels(ownerProducer = {
        requireActivity()
    })

    // Loading dialog
    private val loadingDialog = LoadingDialogFragment()

    // Search filter bottom sheet dialog
    private var filterBottomSheetDialog: FilterBottomSheetDialog? = null

    // Recycler view adapter
    private val resultAdapter = ResultAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        // Set fab icon color
        binding.fabSearchFilter.imageTintList = ColorStateList.valueOf(Color.WHITE)

        // Init search filter bottom sheet dialog
        filterBottomSheetDialog = FilterBottomSheetDialog()
        filterBottomSheetDialog?.setOnResultCallbackListener { filter ->
            viewModel.setFilter(filter)
        }

        // Setup adapter
        resultAdapter.setOnItemClickListener(this::onResultItemClicked)

        // Setup recycler view
        binding.rvResult.layoutManager = LinearLayoutManager(activity)

        // Observe result
        viewModel.listTembang.observe(viewLifecycleOwner, this::listTembangObserver)

        // Observe search filter
        viewModel.filter.observe(viewLifecycleOwner) { filter ->
            viewModel.getTembang(filter)
        }

        // Setup refresh layout listener
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getTembang()
        }

        // Setup button listener
        setupButtonListener()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupButtonListener() {
        // Fab search filter listener
        binding.fabSearchFilter.setOnClickListener {
            // Show search filter bottom sheet navigation
            filterBottomSheetDialog?.show(
                parentFragmentManager,
                FilterBottomSheetDialog::class.java.simpleName
            )
        }
    }

    private fun onResultItemClicked(tembang: Tembang) {
        Intent(activity, DetailActivity::class.java).also {
            it.putExtra(DetailActivity.EXTRA_TEMBANG_UID, tembang.id)
            activity?.startActivity(it)
        }
    }

    private fun listTembangObserver(event: TembangListEvent) {
        when (event) {
            is TembangListEvent.Success -> {
                // Update loading/refresh state
                if (loadingDialog.isVisible) loadingDialog.dismiss()
                if (binding.swipeRefresh.isRefreshing) binding.swipeRefresh.isRefreshing = false

                // Update recycler view & empty container visibility
                binding.emptyContainer.root.visibility = View.GONE
                binding.rvResult.visibility = View.VISIBLE

                // Set result
                resultAdapter.setList(event.listResponse.list)
                binding.rvResult.adapter = resultAdapter
            }
            is TembangListEvent.Error -> {
                // Update loading/refresh state
                if (loadingDialog.isVisible) loadingDialog.dismiss()
                if (binding.swipeRefresh.isRefreshing) binding.swipeRefresh.isRefreshing = false

                // Update recycler view & empty container visibility
                binding.emptyContainer.root.visibility = View.VISIBLE
                binding.rvResult.visibility = View.GONE

                Helpers.snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is TembangListEvent.NetworkError -> {
                // Update loading/refresh state
                if (loadingDialog.isVisible) loadingDialog.dismiss()
                if (binding.swipeRefresh.isRefreshing) binding.swipeRefresh.isRefreshing = false

                // Update recycler view & empty container visibility
                binding.emptyContainer.root.visibility = View.VISIBLE
                binding.rvResult.visibility = View.GONE

                Helpers.snackBarNetworkError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ) {
                    viewModel.getTembang()
                }.show()
            }
            is TembangListEvent.Loading -> {
                if (!loadingDialog.isAdded) {
                    loadingDialog.show(
                        parentFragmentManager,
                        LoadingDialogFragment::class.java.simpleName
                    )
                }
            }
            is TembangListEvent.Empty -> {
                // Update loading/refresh state
                if (loadingDialog.isVisible) loadingDialog.dismiss()
                if (binding.swipeRefresh.isRefreshing) binding.swipeRefresh.isRefreshing = false

                // Update empty container text & remove list from adapter
                binding.emptyContainer.tvEmptyTitle.setText(R.string.result_not_found)
                resultAdapter.removeList()

                // Update recycler view & empty container visibility
                binding.emptyContainer.root.visibility = View.VISIBLE
                binding.rvResult.visibility = View.GONE
            }
        }
    }
}