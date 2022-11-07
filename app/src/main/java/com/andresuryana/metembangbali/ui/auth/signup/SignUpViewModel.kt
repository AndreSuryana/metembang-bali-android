package com.andresuryana.metembangbali.ui.auth.signup

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
class SignUpViewModel @Inject constructor(
    private val repository: MetembangRepository
) : ViewModel() {

    private var _signUpResponse = MutableLiveData<AuthEvent>()
    val signUpResponse: LiveData<AuthEvent> = _signUpResponse

    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            _signUpResponse.value = AuthEvent.Loading
            when (val response = repository.signUp(name, email, password)) {
                is Resource.Success ->
                    _signUpResponse.value = AuthEvent.Success(response.data)
                is Resource.Error ->
                    _signUpResponse.value = AuthEvent.Error(response.message!!)
                is Resource.NetworkError ->
                    _signUpResponse.value = AuthEvent.NetworkError
            }
        }
    }
}