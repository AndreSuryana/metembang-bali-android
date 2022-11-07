package com.andresuryana.metembangbali.utils.event

import com.andresuryana.metembangbali.data.model.Category

sealed class CategoryEvent {
    class Success(val categories: ArrayList<Category>) : CategoryEvent()
    class Error(val message: String) : CategoryEvent()
    object NetworkError : CategoryEvent()
    object Loading : CategoryEvent()
    object Empty : CategoryEvent()
}
