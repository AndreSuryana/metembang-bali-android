package com.andresuryana.metembangbali.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchFilter(

    val category: Category? = null,

    val subCategory: SubCategory? = null,

    val usageType: UsageType? = null,

    val usage: Usage? = null,

    val mood: Mood? = null,

    val rule: Rule? = null

) : Parcelable
