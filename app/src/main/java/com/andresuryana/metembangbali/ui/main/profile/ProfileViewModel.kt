package com.andresuryana.metembangbali.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.metembangbali.data.repository.MetembangRepository
import com.andresuryana.metembangbali.utils.Resource
import com.andresuryana.metembangbali.utils.event.SignOutEvent
import com.andresuryana.metembangbali.utils.event.UpdateAvatarEvent
import com.andresuryana.metembangbali.utils.event.UserEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: MetembangRepository
) : ViewModel() {

    private val _user = MutableLiveData<UserEvent>()
    val user: LiveData<UserEvent> = _user

    private val _signOut = MutableSharedFlow<SignOutEvent>()
    val signOut: SharedFlow<SignOutEvent> = _signOut

    private val _updateAvatar = MutableSharedFlow<UpdateAvatarEvent>()
    val updateAvatar: SharedFlow<UpdateAvatarEvent> = _updateAvatar

    fun fetchUser() {
        viewModelScope.launch {
            _user.value = UserEvent.Loading
            when (val response = repository.fetchUser()) {
                is Resource.Success ->
                    _user.value = UserEvent.Success(response.data)
                is Resource.Error ->
                    _user.value = UserEvent.Error(response.message!!)
                is Resource.NetworkError ->
                    _user.value = UserEvent.NetworkError
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _signOut.emit(SignOutEvent.Loading)
            when (val response = repository.signOut()) {
                is Resource.Success ->
                    _signOut.emit(SignOutEvent.Success)
                is Resource.Error ->
                    _signOut.emit(SignOutEvent.Error(response.message!!))
                is Resource.NetworkError ->
                    _signOut.emit(SignOutEvent.NetworkError)
            }
        }
    }

    fun updateProfileAvatar(image: File) {
        viewModelScope.launch {
            _updateAvatar.emit(UpdateAvatarEvent.Loading)
            when (val response = repository.uploadUserPhoto(image)) {
                is Resource.Success ->
                    _updateAvatar.emit(UpdateAvatarEvent.Success)
                is Resource.Error ->
                    _updateAvatar.emit(UpdateAvatarEvent.Error(response.message!!))
                is Resource.NetworkError ->
                    _updateAvatar.emit(UpdateAvatarEvent.NetworkError)
            }
        }
    }
}