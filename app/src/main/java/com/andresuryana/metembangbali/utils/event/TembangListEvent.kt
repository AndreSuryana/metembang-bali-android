package com.andresuryana.metembangbali.utils.event

import com.andresuryana.metembangbali.data.model.ListResponse
import com.andresuryana.metembangbali.data.model.Tembang

sealed class TembangListEvent {
    class Success(val listResponse: ListResponse<Tembang>) : TembangListEvent()
    class Error(val message: String) : TembangListEvent()
    object NetworkError : TembangListEvent()
    object Loading : TembangListEvent()
    object Empty : TembangListEvent()
}
