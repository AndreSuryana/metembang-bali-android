package com.andresuryana.metembangbali.utils.event

import com.andresuryana.metembangbali.data.model.SubCategory

sealed class SubCategoryEvent {
    class Success(val subCategories: ArrayList<SubCategory>) : SubCategoryEvent()
    class Error(val message: String) : SubCategoryEvent()
    object NetworkError : SubCategoryEvent()
    object Loading : SubCategoryEvent()
    object Empty : SubCategoryEvent()
}
