package com.andresuryana.metembangbali.utils.event

import com.andresuryana.metembangbali.data.model.Usage

sealed class UsageEvent {
    class Success(val usages: ArrayList<Usage>) : UsageEvent()
    class Error(val message: String) : UsageEvent()
    object NetworkError : UsageEvent()
    object Loading : UsageEvent()
    object Empty : UsageEvent()
}
