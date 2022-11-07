package com.andresuryana.metembangbali.utils.event

sealed class DeleteSubmissionEvent {
    class Success(val isDeleted: Boolean) : DeleteSubmissionEvent()
    class Error(val message: String) : DeleteSubmissionEvent()
    object NetworkError : DeleteSubmissionEvent()
    object Loading : DeleteSubmissionEvent()
}
