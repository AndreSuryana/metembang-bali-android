package com.andresuryana.metembangbali.ui.main.search

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
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
import com.andresuryana.metembangbali.utils.sorting.SelectionSort
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    // Layout binding
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding

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

    // Popup menu
    private var popupMenu: PopupMenu? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        // Set fab icon color
        binding?.fabSearchFilter?.imageTintList = ColorStateList.valueOf(Color.WHITE)

        // Init search filter bottom sheet dialog
        filterBottomSheetDialog = FilterBottomSheetDialog()
        filterBottomSheetDialog?.setOnResultCallbackListener { filter ->
            viewModel.setFilter(filter)
        }

        // Init popup menu
        initPopupMenu()

        // Setup adapter
        resultAdapter.setOnItemClickListener(this::onResultItemClicked)

        // Setup recycler view
        binding?.rvResult?.layoutManager = LinearLayoutManager(activity)

        // Observe result
        viewModel.listTembang.observe(viewLifecycleOwner, this::listTembangObserver)

        // Observe search filter
        viewModel.filter.observe(viewLifecycleOwner) { filter ->
            viewModel.getTembang(filter)
        }

        // Observe list
        viewModel.list.observe(requireActivity()) {
            // Set result
            resultAdapter.setList(it)
            binding?.rvResult?.adapter = resultAdapter

            // Reset selected sort method
            resetSortMenu()
        }

        // Setup refresh layout listener
        binding?.swipeRefresh?.setOnRefreshListener {
            viewModel.getTembang()
        }

        // Setup button listener
        setupButtonListener()

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupButtonListener() {
        // Fab search filter listener
        binding?.fabSearchFilter?.setOnClickListener {
            // Show search filter bottom sheet navigation
            filterBottomSheetDialog?.show(
                parentFragmentManager,
                FilterBottomSheetDialog::class.java.simpleName
            )
        }

        // Sort button listener
        binding?.btnSort?.setOnClickListener {
            // Show popup menu
            popupMenu?.show()
        }
    }

    private fun initPopupMenu() {
        popupMenu = binding?.btnSort?.let { btnSort ->
            PopupMenu(requireContext(), btnSort, Gravity.END).apply {
                inflate(R.menu.menu_sorting)
                setOnMenuItemClickListener {
                    onMenuSortClickListener(it)
                    true
                }
            }
        }
    }

    private fun onMenuSortClickListener(menu: MenuItem) {
        when (menu.itemId) {
            R.id.menu_sort_by_title_asc -> {
                viewModel.sortByTitle(SelectionSort.Method.ASC)
            }
            R.id.menu_sort_by_title_desc -> {
                viewModel.sortByTitle(SelectionSort.Method.DESC)
            }
            R.id.menu_sort_by_date_asc -> {
                viewModel.sortByDate(SelectionSort.Method.ASC)
            }
            R.id.menu_sort_by_date_desc -> {
                viewModel.sortByDate(SelectionSort.Method.DESC)
            }
        }
        popupMenu?.menu?.findItem(menu.itemId)?.isChecked = true
    }

    private fun resetSortMenu() {
        // Reset to default
        popupMenu?.menu?.findItem(R.id.menu_sort_by_date_desc)?.isChecked = true
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
                if (binding?.swipeRefresh?.isRefreshing == true) binding?.swipeRefresh?.isRefreshing = false

                hideEmptyContainer()
            }
            is TembangListEvent.Error -> {
                // Update loading/refresh state
                if (loadingDialog.isVisible) loadingDialog.dismiss()
                if (binding?.swipeRefresh?.isRefreshing == true) binding?.swipeRefresh?.isRefreshing = false

                showEmptyContainer(R.string.result_not_found)

                binding?.root?.let { Helpers.snackBarError(it, event.message, Snackbar.LENGTH_SHORT).show() }
            }
            is TembangListEvent.NetworkError -> {
                // Update loading/refresh state
                if (loadingDialog.isVisible) loadingDialog.dismiss()
                if (binding?.swipeRefresh?.isRefreshing == true) binding?.swipeRefresh?.isRefreshing = false

                showEmptyContainer(R.string.result_not_found)

                binding?.root?.let {
                    Helpers.snackBarNetworkError(
                        it,
                        getString(R.string.error_default_network_error),
                        Snackbar.LENGTH_SHORT
                    ) {
                        viewModel.getTembang()
                    }.show()
                }
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
                if (binding?.swipeRefresh?.isRefreshing == true) binding?.swipeRefresh?.isRefreshing = false

                showEmptyContainer(R.string.result_not_found)
                resultAdapter.removeList()
            }
        }
    }

    private fun showEmptyContainer(stringRes: Int?) {
        if (stringRes != null) {
            binding?.emptyContainer?.tvEmptyTitle?.setText(stringRes)
        }
        binding?.emptyContainer?.root?.visibility = View.VISIBLE
        binding?.rvResult?.visibility = View.GONE
    }

    private fun hideEmptyContainer() {
        binding?.emptyContainer?.root?.visibility = View.GONE
        binding?.rvResult?.visibility = View.VISIBLE
    }
}