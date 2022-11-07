package com.andresuryana.metembangbali.utils.event

import com.andresuryana.metembangbali.data.model.Tembang

sealed class HomeEvent {
    class Success(val latest: ArrayList<Tembang>, val topMostViewed: ArrayList<Tembang>) : HomeEvent()
    class Error(val message: String) : HomeEvent()
    object NetworkError : HomeEvent()
    object Loading : HomeEvent()
    object Empty : HomeEvent()
}
