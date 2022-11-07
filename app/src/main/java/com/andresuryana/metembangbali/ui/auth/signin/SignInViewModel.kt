package com.andresuryana.metembangbali.ui.auth.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.metembangbali.data.repository.MetembangRepository
import com.andresuryana.metembangbali.utils.Resource
import com.andresuryana.metembangbali.utils.event.AuthEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: MetembangRepository
) : ViewModel() {

    private val _signInResponse = MutableLiveData<AuthEvent>()
    val signInResponse: LiveData<AuthEvent> = _signInResponse

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _signInResponse.value = AuthEvent.Loading
            when (val response = repository.signIn(email, password)) {
                is Resource.Success ->
                    _signInResponse.value = AuthEvent.Success(response.data)
                is Resource.Error ->
                    _signInResponse.value = AuthEvent.Error(response.message!!)
                is Resource.NetworkError ->
                    _signInResponse.value = AuthEvent.NetworkError
            }
        }
    }
}