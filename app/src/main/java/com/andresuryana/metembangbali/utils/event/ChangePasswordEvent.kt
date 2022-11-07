package com.andresuryana.metembangbali.utils.event

sealed class ChangePasswordEvent {
    class Success(val isChanged: Boolean) : ChangePasswordEvent()
    class Error(val message: String) : ChangePasswordEvent()
    object NetworkError : ChangePasswordEvent()
    object Loading : ChangePasswordEvent()
}
