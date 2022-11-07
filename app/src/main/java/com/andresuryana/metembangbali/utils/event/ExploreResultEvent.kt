package com.andresuryana.metembangbali.utils.event

import com.andresuryana.metembangbali.data.model.ListResponse
import com.andresuryana.metembangbali.data.model.Tembang

sealed class ExploreResultEvent {
    class Success(val listResponse: ListResponse<Tembang>) : ExploreResultEvent()
    class Error(val message: String) : ExploreResultEvent()
    object NetworkError : ExploreResultEvent()
    object Loading : ExploreResultEvent()
    object Empty : ExploreResultEvent()
}
