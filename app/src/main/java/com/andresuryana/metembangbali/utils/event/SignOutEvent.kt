package com.andresuryana.metembangbali.utils.event

sealed class SignOutEvent {
    class Success(val isLogout: Boolean) : SignOutEvent()
    class Error(val message: String) : SignOutEvent()
    object NetworkError : SignOutEvent()
    object Loading : SignOutEvent()
}
