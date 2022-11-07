package com.andresuryana.metembangbali.utils.event

import com.andresuryana.metembangbali.data.model.Submission

sealed class SubmissionEvent {
    class Success(val submission: Submission) : SubmissionEvent()
    class Error(val message: String) : SubmissionEvent()
    object NetworkError : SubmissionEvent()
    object Loading : SubmissionEvent()
}
