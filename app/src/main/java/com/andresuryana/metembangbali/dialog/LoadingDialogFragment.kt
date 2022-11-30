package com.andresuryana.metembangbali.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.databinding.FragmentLoadingDialogBinding

class LoadingDialogFragment : DialogFragment() {

    private var _binding: FragmentLoadingDialogBinding? = null
    private val binding get() = _binding!!

    init {
        this.isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout
        _binding = FragmentLoadingDialogBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_rectangle)
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (isVisible) dismiss()
        else super.onDismiss(dialog)
    }
}