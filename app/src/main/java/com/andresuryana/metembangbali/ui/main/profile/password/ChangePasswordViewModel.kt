package com.andresuryana.metembangbali.ui.main.profile.password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.metembangbali.data.repository.MetembangRepository
import com.andresuryana.metembangbali.utils.Resource
import com.andresuryana.metembangbali.utils.event.ChangePasswordEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val repository: MetembangRepository
) : ViewModel() {

    private val _changePassword = MutableSharedFlow<ChangePasswordEvent>()
    val changePassword: SharedFlow<ChangePasswordEvent> = _changePassword

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            _changePassword.emit(ChangePasswordEvent.Loading)
            when (val response =
                repository.changePassword(oldPassword, newPassword, confirmPassword)) {
                is Resource.Success ->
                    _changePassword.emit(ChangePasswordEvent.Success)
                is Resource.Error ->
                    _changePassword.emit(ChangePasswordEvent.Error(response.message!!))
                is Resource.NetworkError ->
                    _changePassword.emit(ChangePasswordEvent.NetworkError)
            }
        }
    }
}