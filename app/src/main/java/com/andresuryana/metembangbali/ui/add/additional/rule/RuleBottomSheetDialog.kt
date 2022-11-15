package com.andresuryana.metembangbali.ui.add.additional.rule

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.data.model.Rule
import com.andresuryana.metembangbali.databinding.FragmentRuleBottomSheetDialogBinding
import com.andresuryana.metembangbali.utils.Ext.toCharArray
import com.andresuryana.metembangbali.utils.Ext.toIntegerArray
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RuleBottomSheetDialog : BottomSheetDialogFragment() {

    // Layout binding
    private var _binding: FragmentRuleBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

    // Callback
    private var onResultCallback: ((rule: Rule) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout
        _binding = FragmentRuleBottomSheetDialogBinding.inflate(layoutInflater)

        // Button set filter color
        binding.btnAddRule.apply {
            setTextColor(ColorStateList.valueOf(Color.WHITE))
            backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.color_primary)
            )
        }

        // Button add rule listener
        binding.btnAddRule.setOnClickListener {
            onAddRule()
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup sheet behaviour
        BottomSheetBehavior.from(view.parent as View)
            .state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun setOnResultCallbackListener(onResultCallback: (rule: Rule) -> Unit) {
        this.onResultCallback = onResultCallback
    }

    private fun onAddRule() {
        // Define array
        val guruWilangArray = ArrayList<Int>()
        val guruDingdongArray = ArrayList<Char>()

        // Get value
        val ruleName = binding.etRuleName.text?.trim().toString()
        val guruGatra = binding.etGuruGatra.text?.trim().toString().toIntOrNull()
        val guruWilang = binding.etGuruWilang.text?.trim().toString()
        val guruDingdong = binding.etGuruDingdong.text?.trim().toString()

        // Clear array
        guruWilangArray.clear()
        guruDingdongArray.clear()

        // Add array
        guruWilang.toIntegerArray(',')?.let { guruWilangArray.addAll(it) }
        guruDingdong.toCharArray(',')?.let { guruDingdongArray.addAll(it) }

        // Reset helper text
        binding.apply {
            tilRuleName.helperText = ""
            tilGuruGatra.helperText = ""
            tilGuruWilang.helperText = ""
            tilGuruDingdong.helperText = ""
        }

        // Validation
        if (ruleName.isBlank()) {
            binding.tilRuleName.apply {
                helperText = getString(R.string.helper_empty_rule_name)
                requestFocus()
            }
            return
        }

        if (guruGatra == null) {
            binding.tilGuruGatra.apply {
                helperText = getString(R.string.helper_empty_guru_gatra)
                requestFocus()
            }
            return
        }

        if (guruWilang.isBlank()) {
            binding.tilGuruWilang.apply {
                helperText = getString(R.string.helper_empty_guru_wilang)
                requestFocus()
            }
            return
        } else {
            if (guruWilangArray.isEmpty()) {
                binding.tilGuruWilang.apply {
                    helperText = getString(R.string.helper_empty_guru_wilang)
                    requestFocus()
                }
                return
            } else if (guruWilangArray.size != guruGatra) {
                binding.tilGuruWilang.apply {
                    helperText = getString(
                        R.string.helper_guru_wilang_not_match,
                        guruWilangArray.size,
                        guruGatra
                    )
                    requestFocus()
                }
                return
            }
        }

        if (guruDingdong.isBlank()) {
            binding.tilGuruDingdong.apply {
                helperText = getString(R.string.helper_empty_guru_dingdong)
                requestFocus()
            }
            return
        } else {
            if (guruDingdongArray.isEmpty()) {
                binding.tilGuruDingdong.apply {
                    helperText = getString(R.string.helper_empty_guru_dingdong)
                    requestFocus()
                }
                return
            } else if (guruDingdongArray.size != guruGatra) {
                binding.tilGuruDingdong.apply {
                    helperText = getString(
                        R.string.helper_guru_dingdong_not_match,
                        guruDingdongArray.size,
                        guruGatra
                    )
                    requestFocus()
                }
                return
            }
        }

        // Set current rule
        onResultCallback?.invoke(Rule(null, ruleName, guruGatra, guruDingdong, guruWilang))

        // Dismiss bottom sheet dialog
        dismiss()
    }
}