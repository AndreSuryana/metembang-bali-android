package com.andresuryana.metembangbali.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.metembangbali.data.repository.MetembangRepository
import com.andresuryana.metembangbali.utils.Resource
import com.andresuryana.metembangbali.utils.event.TembangListEvent
import com.andresuryana.metembangbali.utils.event.UserEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MetembangRepository
) : ViewModel() {

    private var _user = MutableLiveData<UserEvent>()
    val user: LiveData<UserEvent> = _user

    private var _latest = MutableLiveData<TembangListEvent>()
    val latest: LiveData<TembangListEvent> = _latest

    private var _topMostViewed = MutableLiveData<TembangListEvent>()
    val topMostViewed: LiveData<TembangListEvent> = _topMostViewed

    fun getUser() {
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

    fun getLatest() {
        viewModelScope.launch {
            _latest.value = TembangListEvent.Loading
            when (val response = repository.latest()) {
                is Resource.Success -> {
                    if (response.data.size == 0) _latest.value = TembangListEvent.Empty
                    else _latest.value = TembangListEvent.Success(response.data)
                }
                is Resource.Error ->
                    _latest.value = TembangListEvent.Error(response.message!!)
                is Resource.NetworkError ->
                    _latest.value = TembangListEvent.NetworkError
            }
        }
    }

    fun getTopMostViewed() {
        viewModelScope.launch {
            _topMostViewed.value = TembangListEvent.Loading
            when (val response = repository.topMostViewed()) {
                is Resource.Success -> {
                    if (response.data.size == 0) _topMostViewed.value = TembangListEvent.Empty
                    else _topMostViewed.value = TembangListEvent.Success(response.data)
                }
                is Resource.Error ->
                    _topMostViewed.value = TembangListEvent.Error(response.message!!)
                is Resource.NetworkError ->
                    _topMostViewed.value = TembangListEvent.NetworkError
            }
        }
    }
}