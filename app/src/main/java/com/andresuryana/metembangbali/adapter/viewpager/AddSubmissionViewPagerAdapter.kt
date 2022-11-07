package com.andresuryana.metembangbali.adapter.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.andresuryana.metembangbali.ui.add.additional.AdditionalFragment
import com.andresuryana.metembangbali.ui.add.general.GeneralFragment
import com.andresuryana.metembangbali.ui.add.media.MediaFragment

class AddSubmissionViewPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = SIZE

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            GENERAL -> GeneralFragment()
            ADDITIONAL -> AdditionalFragment()
            MEDIA -> MediaFragment()
            else -> GeneralFragment()
        }
    }

    companion object {
        const val GENERAL = 0
        const val ADDITIONAL = 1
        const val MEDIA = 2
        const val SIZE = 3
    }
}