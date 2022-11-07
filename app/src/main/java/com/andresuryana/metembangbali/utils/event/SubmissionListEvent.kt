package com.andresuryana.metembangbali.utils.event

import com.andresuryana.metembangbali.data.model.ListResponse
import com.andresuryana.metembangbali.data.model.Submission

sealed class SubmissionListEvent {
    class Success(val listResponse: ListResponse<Submission>) : SubmissionListEvent()
    class Error(val message: String) : SubmissionListEvent()
    object NetworkError : SubmissionListEvent()
    object Loading : SubmissionListEvent()
    object Empty : SubmissionListEvent()
}
