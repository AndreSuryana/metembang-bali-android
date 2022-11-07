package com.andresuryana.metembangbali.utils.event

import com.andresuryana.metembangbali.data.model.Tembang

sealed class TembangDetailEvent {
    class Success(val tembang: Tembang) : TembangDetailEvent()
    class Error(val message: String) : TembangDetailEvent()
    object NetworkError : TembangDetailEvent()
    object Loading : TembangDetailEvent()
}
