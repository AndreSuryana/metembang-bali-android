package com.andresuryana.metembangbali.utils.event

sealed class UpdateAvatarEvent {
    object Success : UpdateAvatarEvent()
    class Error(val message: String) : UpdateAvatarEvent()
    object NetworkError : UpdateAvatarEvent()
    object Loading : UpdateAvatarEvent()
}
