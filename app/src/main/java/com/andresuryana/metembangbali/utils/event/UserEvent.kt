package com.andresuryana.metembangbali.utils.event

import com.andresuryana.metembangbali.data.model.User

sealed class UserEvent {
    class Success(val user: User) : UserEvent()
    class Error(val message: String) : UserEvent()
    object NetworkError : UserEvent()
    object Loading : UserEvent()
}
