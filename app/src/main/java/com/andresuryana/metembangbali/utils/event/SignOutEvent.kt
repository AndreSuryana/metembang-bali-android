package com.andresuryana.metembangbali.utils.event

sealed class SignOutEvent {
    object Success : SignOutEvent()
    class Error(val message: String) : SignOutEvent()
    object NetworkError : SignOutEvent()
    object Loading : SignOutEvent()
}
