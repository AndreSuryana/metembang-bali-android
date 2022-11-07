package com.andresuryana.metembangbali.utils.event

import com.andresuryana.metembangbali.data.model.UsageType

sealed class UsageTypeEvent {
    class Success(val usageTypes: ArrayList<UsageType>) : UsageTypeEvent()
    class Error(val message: String) : UsageTypeEvent()
    object NetworkError : UsageTypeEvent()
    object Loading : UsageTypeEvent()
    object Empty : UsageTypeEvent()
}
