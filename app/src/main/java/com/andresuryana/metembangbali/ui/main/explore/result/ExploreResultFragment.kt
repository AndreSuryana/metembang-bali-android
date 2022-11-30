package com.andresuryana.metembangbali.ui.main.explore.result

import android.content.Intent
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
import com.andresuryana.metembangbali.databinding.FragmentExploreResultBinding
import com.andresuryana.metembangbali.helper.Helpers
import com.andresuryana.metembangbali.ui.main.detail.DetailActivity
import com.andresuryana.metembangbali.utils.event.ExploreResultEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreResultFragment : Fragment() {

    // Layout binding
    private var _binding: FragmentExploreResultBinding? = null
    private val binding get() = _binding!!

    // View model
    private val viewModel: ExploreResultViewModel by viewModels()

    // Recycler view adapter
    private val resultAdapter = ResultAdapter()

    // Category name
    private var categoryId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout
        _binding = FragmentExploreResultBinding.inflate(inflater, container, false)

        // Get category name from arguments
        categoryId = arguments?.getString(EXTRA_CATEGORY_ID)

        // Setup adapter
        resultAdapter.setOnItemClickListener(this::onResultItemClicked)

        // Setup recycler view
        binding.rvResult.layoutManager = LinearLayoutManager(activity)

        // Get & observe result
        viewModel.getTembang(categoryId)
        viewModel.listTembang.observe(viewLifecycleOwner, this::listTembangObserver)

        // Setup button listener
        setupButtonListener()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupButtonListener() {
        // Button back listener
        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun onResultItemClicked(tembang: Tembang) {
        Intent(activity, DetailActivity::class.java).also {
            it.putExtra(DetailActivity.EXTRA_TEMBANG_UID, tembang.id)
            activity?.startActivity(it)
        }
    }

    private fun listTembangObserver(event: ExploreResultEvent) {
        when (event) {
            is ExploreResultEvent.Success -> {
                resultAdapter.setList(event.listResponse.list)
                binding.rvResult.adapter = resultAdapter

                // Update recycler view & empty container visibility
                binding.emptyContainer.root.visibility = View.GONE
                binding.rvResult.visibility = View.VISIBLE
            }
            is ExploreResultEvent.Error -> {
                // Update recycler view & empty container visibility
                binding.emptyContainer.root.visibility = View.VISIBLE
                binding.rvResult.visibility = View.GONE

                Helpers.snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is ExploreResultEvent.NetworkError -> {
                // Update recycler view & empty container visibility
                binding.emptyContainer.root.visibility = View.VISIBLE
                binding.rvResult.visibility = View.GONE

                Helpers.snackBarNetworkError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ) {
                    viewModel.getTembang(categoryId)
                }.show()
            }
            is ExploreResultEvent.Loading -> {}
            is ExploreResultEvent.Empty -> {
                // Update recycler view & empty container visibility
                binding.emptyContainer.root.visibility = View.VISIBLE
                binding.rvResult.visibility = View.GONE
            }
        }
    }

    companion object {
        const val EXTRA_CATEGORY_ID = "category_id"
    }
}