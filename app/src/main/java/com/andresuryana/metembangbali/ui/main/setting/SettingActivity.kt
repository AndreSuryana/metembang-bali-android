package com.andresuryana.metembangbali.ui.main.setting

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.databinding.ActivitySettingBinding
import com.andresuryana.metembangbali.helper.Helpers.snackBarSuccess
import com.andresuryana.metembangbali.utils.Constants.APP_FONT
import com.andresuryana.metembangbali.utils.Constants.SHARED_PREFS_KEY
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {

    // Layout binding
    private lateinit var binding: ActivitySettingBinding

    // Shared prefs
    private lateinit var prefs: SharedPreferences

    // Load selectedFontSizeIndex in prefs
    private var selectedFontSizeIndex: Int = 1 /* Normal */

    // Font size list
    private lateinit var fontSizes: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load font size list
        fontSizes = resources.getStringArray(R.array.font_size_list)

        // Init current setting values
        prefs = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)
        selectedFontSizeIndex = prefs.getInt(APP_FONT, 1 /* Normal */)

        // Set font size text view
        binding.tvFontSize.text = fontSizes[selectedFontSizeIndex]

        // Setup button listener
        setupButtonListener()
    }

    private fun setupButtonListener() {
        // Button back listener
        binding.btnBack.setOnClickListener { finish() }

        // Button font size listener
        binding.settingFontSize.setOnClickListener {
            showFontSizeRadioDialog()
            // Update font size text view
            binding.tvFontSize.text = resources.getStringArray(R.array.font_size_list)[selectedFontSizeIndex]
        }
    }

    private fun showFontSizeRadioDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.menu_setting_font_size)
            .setSingleChoiceItems(fontSizes, selectedFontSizeIndex) { _, selected ->
                // Update font size in prefs
                prefs.edit().putInt(APP_FONT, selected).commit()
                selectedFontSizeIndex = selected
            }
            .setPositiveButton(R.string.answer_yes) { dialog, _ ->
                // Update font size text view
                this.binding.tvFontSize.text = fontSizes[selectedFontSizeIndex]

                // Success & dismiss
                snackBarSuccess(binding.root, getString(R.string.success_change_font_size)).show()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.answer_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}