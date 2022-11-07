package com.andresuryana.metembangbali.utils.data

import com.andresuryana.metembangbali.data.model.Category
import com.andresuryana.metembangbali.data.model.SubCategory

data class GeneralData(

    val title: String,

    val category: Category? = null,

    val subCategory: SubCategory? = null,

    val lyrics: ArrayList<String>
)
