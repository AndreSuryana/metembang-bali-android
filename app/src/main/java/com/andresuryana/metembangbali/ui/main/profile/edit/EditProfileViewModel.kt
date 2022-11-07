package com.andresuryana.metembangbali.ui.main.profile.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.metembangbali.data.repository.MetembangRepository
import com.andresuryana.metembangbali.utils.Resource
import com.andresuryana.metembangbali.utils.event.UserEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: MetembangRepository
) : ViewModel() {

    private val _user = MutableSharedFlow<UserEvent>()
    val user: SharedFlow<UserEvent> = _user

    private val _updateUser = MutableLiveData<UserEvent>()
    val updateUser: LiveData<UserEvent> = _updateUser

    init {
        fetchUser()
    }

    fun updateUser(
        name: String,
        email: String,
        phone: String,
        address: String,
        occupation: String? = null
    ) {
        viewModelScope.launch {
            _updateUser.value = UserEvent.Loading
            when (val response = repository.updateUser(name, email, phone, address, occupation)) {
                is Resource.Success ->
                    _updateUser.value = UserEvent.Success(response.data)
                is Resource.Error ->
                    _updateUser.value = UserEvent.Error(response.message!!)
                is Resource.NetworkError ->
                    _updateUser.value = UserEvent.NetworkError
            }
        }
    }

    fun fetchUser() {
        viewModelScope.launch {
            _user.emit(UserEvent.Loading)
            when (val response = repository.fetchUser()) {
                is Resource.Success ->
                    _user.emit(UserEvent.Success(response.data))
                is Resource.Error ->
                    _user.emit(UserEvent.Error(response.message!!))
                is Resource.NetworkError ->
                    _user.emit(UserEvent.NetworkError)
            }
        }
    }
}