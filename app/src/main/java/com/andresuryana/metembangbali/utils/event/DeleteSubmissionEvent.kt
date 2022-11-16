package com.andresuryana.metembangbali.utils.event

sealed class DeleteSubmissionEvent {
    object Success : DeleteSubmissionEvent()
    class Error(val message: String) : DeleteSubmissionEvent()
    object NetworkError : DeleteSubmissionEvent()
    object Loading : DeleteSubmissionEvent()
}
