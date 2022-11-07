package com.andresuryana.metembangbali.utils.event

import com.andresuryana.metembangbali.data.model.AuthResponse

sealed class AuthEvent {
    class Success(val authResponse: AuthResponse) : AuthEvent()
    class Error(val message: String) : AuthEvent()
    object NetworkError : AuthEvent()
    object Loading : AuthEvent()
}
