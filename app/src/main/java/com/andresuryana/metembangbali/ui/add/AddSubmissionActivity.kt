package com.andresuryana.metembangbali.ui.add

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.adapter.viewpager.AddSubmissionViewPagerAdapter
import com.andresuryana.metembangbali.adapter.viewpager.AddSubmissionViewPagerAdapter.Companion.ADDITIONAL
import com.andresuryana.metembangbali.adapter.viewpager.AddSubmissionViewPagerAdapter.Companion.GENERAL
import com.andresuryana.metembangbali.adapter.viewpager.AddSubmissionViewPagerAdapter.Companion.MEDIA
import com.andresuryana.metembangbali.databinding.ActivityAddSubmissionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddSubmissionActivity : AppCompatActivity() {

    // Layout binding
    private lateinit var binding: ActivityAddSubmissionBinding

    // View model
    private val viewModel: AddSubmissionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout
        binding = ActivityAddSubmissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup view pager
        binding.viewPager.apply {
            adapter = AddSubmissionViewPagerAdapter(this@AddSubmissionActivity)
            isUserInputEnabled = false
        }

        // Observe current page position
        viewModel.currentPage.observe(this) { position ->
            Log.d("ViewPager", "currentPage=$position")
            binding.viewPager.currentItem = position
            setStepperState(position)
        }

        // Setup button listener
        setupButtonListener()

        /** Divide into sections :
         * - General Information (title, category, sub_category, lyrics: -> add by each line) -> dropdown except title
         * - Additional Information (meaning, mood, rule, usage, lyrics_id) -> dropdown
         * - Media Data (cover: jpg,jpeg,png -> camera/file, cover_source, audio: mp3 -> record/file)
         */
    }

    override fun onBackPressed() {
        val currentPosition = binding.viewPager.currentItem
        if (currentPosition > 0) {
            viewModel.setPagePosition(currentPosition - 1)
        } else {
            super.onBackPressed()
        }
    }

    private fun setStepperState(position: Int) {
        // Color variables
        val colorPrimary = ContextCompat.getColor(this, R.color.color_primary)
        val colorSecondary = ContextCompat.getColor(this, R.color.color_secondary)

        when (position) {
            GENERAL -> {
                binding.stepper.stepGeneral.apply {
                    setTextColor(ColorStateList.valueOf(colorPrimary))
                    compoundDrawablesRelative.getOrNull(0)?.setTint(colorPrimary)
                }
                binding.stepper.stepAdditional.apply {
                    setTextColor(ColorStateList.valueOf(colorSecondary))
                    compoundDrawablesRelative.getOrNull(0)?.setTint(colorSecondary)
                }
                binding.stepper.stepMedia.apply {
                    setTextColor(ColorStateList.valueOf(colorSecondary))
                    compoundDrawablesRelative.getOrNull(0)?.setTint(colorSecondary)
                }
                binding.stepper.divider1.setBackgroundColor(colorSecondary)
                binding.stepper.divider2.setBackgroundColor(colorSecondary)
            }
            ADDITIONAL -> {
                binding.stepper.stepGeneral.apply {
                    setTextColor(ColorStateList.valueOf(colorPrimary))
                    compoundDrawablesRelative.getOrNull(0)?.setTint(colorPrimary)
                }
                binding.stepper.stepAdditional.apply {
                    setTextColor(ColorStateList.valueOf(colorPrimary))
                    compoundDrawablesRelative.getOrNull(0)?.setTint(colorPrimary)
                }
                binding.stepper.stepMedia.apply {
                    setTextColor(ColorStateList.valueOf(colorSecondary))
                    compoundDrawablesRelative.getOrNull(0)?.setTint(colorSecondary)
                }
                binding.stepper.divider1.setBackgroundColor(colorPrimary)
                binding.stepper.divider2.setBackgroundColor(colorSecondary)
            }
            MEDIA -> {
                binding.stepper.stepGeneral.apply {
                    setTextColor(ColorStateList.valueOf(colorPrimary))
                    compoundDrawablesRelative.getOrNull(0)?.setTint(colorPrimary)
                }
                binding.stepper.stepAdditional.apply {
                    setTextColor(ColorStateList.valueOf(colorPrimary))
                    compoundDrawablesRelative.getOrNull(0)?.setTint(colorPrimary)
                }
                binding.stepper.stepMedia.apply {
                    setTextColor(ColorStateList.valueOf(colorPrimary))
                    compoundDrawablesRelative.getOrNull(0)?.setTint(colorPrimary)
                }
                binding.stepper.divider1.setBackgroundColor(colorPrimary)
                binding.stepper.divider2.setBackgroundColor(colorPrimary)
            }
        }
    }

    private fun setupButtonListener() {
        // Button back listener
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}