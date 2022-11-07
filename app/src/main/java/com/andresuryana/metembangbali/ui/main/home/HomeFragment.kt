package com.andresuryana.metembangbali.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.adapter.LatestAdapter
import com.andresuryana.metembangbali.adapter.MostViewAdapter
import com.andresuryana.metembangbali.data.model.Tembang
import com.andresuryana.metembangbali.databinding.FragmentHomeBinding
import com.andresuryana.metembangbali.helper.Helpers
import com.andresuryana.metembangbali.ui.main.detail.DetailActivity
import com.andresuryana.metembangbali.utils.Constants.REGISTERED_USER
import com.andresuryana.metembangbali.utils.SessionManager
import com.andresuryana.metembangbali.utils.event.TembangListEvent
import com.andresuryana.metembangbali.utils.event.UserEvent
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    // Layout binding
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // View model
    private val viewModel: HomeViewModel by viewModels()

    // Recycler view adapter
    private lateinit var latestAdapter: LatestAdapter
    private lateinit var mostViewAdapter: MostViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Setup adapter
        setupRecyclerAdapter()

        // Setup recycler view
        setupRecyclerView()

        // Get user, latest, and most viewed
        if (SessionManager(requireContext()).getAuthStatus() == REGISTERED_USER) {
            viewModel.getUser()
        }
        viewModel.getLatest()
        viewModel.getTopMostViewed()

        // Observe user, latest, and most viewed
        viewModel.user.observe(viewLifecycleOwner, this::userObserver)
        viewModel.latest.observe(viewLifecycleOwner, this::latestObserver)
        viewModel.topMostViewed.observe(viewLifecycleOwner, this::topMostViewedObserver)

        Log.d("User", "role: ${SessionManager(requireContext()).getAuthStatus()}")

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerAdapter() {
        // Latest adapter
        latestAdapter = LatestAdapter()
        latestAdapter.setOnItemClickListener(this::onTembangClicked)

        // Most view adapter
        mostViewAdapter = MostViewAdapter()
        mostViewAdapter.setOnItemClickListener(this::onTembangClicked)
    }

    private fun onTembangClicked(tembang: Tembang) {
        Intent(activity, DetailActivity::class.java).also {
            it.putExtra(DetailActivity.EXTRA_TEMBANG_UID, tembang.id)
            activity?.startActivity(it)
        }
    }

    private fun setupRecyclerView() {
        // Latest recycler view
        binding.rvLatest.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        // Most view recycler view
        binding.rvMostViewed.layoutManager =
            LinearLayoutManager(activity)
    }

    private fun userObserver(event: UserEvent) {
        when (event) {
            is UserEvent.Success -> {
                Glide.with(requireContext())
                    .load(event.user.photoUrl)
                    .placeholder(R.drawable.profile_avatar_placeholder)
                    .error(R.drawable.profile_avatar_placeholder)
                    .centerCrop()
                    .into(binding.ivProfileAvatar)
            }
            is UserEvent.Error -> {
                Helpers.snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is UserEvent.NetworkError -> {
                Helpers.snackBarError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            is UserEvent.Loading -> {}
        }
    }

    private fun latestObserver(event: TembangListEvent) {
        when (event) {
            is TembangListEvent.Success -> {
                latestAdapter.setList(event.listResponse.list)
                binding.rvLatest.adapter = latestAdapter
            }
            is TembangListEvent.Error -> {
                Helpers.snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is TembangListEvent.NetworkError -> {
                Helpers.snackBarNetworkError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ) {
                    viewModel.getLatest()
                }.show()
            }
            is TembangListEvent.Loading -> {}
            is TembangListEvent.Empty -> {
                // TODO : Create empty layout
            }
        }
    }

    private fun topMostViewedObserver(event: TembangListEvent) {
        when (event) {
            is TembangListEvent.Success -> {
                mostViewAdapter.setList(event.listResponse.list)
                binding.rvMostViewed.adapter = mostViewAdapter
            }
            is TembangListEvent.Error -> {
                Helpers.snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is TembangListEvent.NetworkError -> {
                Helpers.snackBarNetworkError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ) {
                    viewModel.getTopMostViewed()
                }.show()
            }
            is TembangListEvent.Loading -> {}
            is TembangListEvent.Empty -> {
                // TODO : Create empty layout
            }
        }
    }
}