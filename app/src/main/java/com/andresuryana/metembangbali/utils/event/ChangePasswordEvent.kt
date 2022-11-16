package com.andresuryana.metembangbali.utils.event

sealed class ChangePasswordEvent {
    object Success : ChangePasswordEvent()
    class Error(val message: String) : ChangePasswordEvent()
    object NetworkError : ChangePasswordEvent()
    object Loading : ChangePasswordEvent()
}
