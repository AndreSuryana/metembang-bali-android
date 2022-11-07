package com.andresuryana.metembangbali.ui.main.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.metembangbali.data.repository.MetembangRepository
import com.andresuryana.metembangbali.utils.Resource
import com.andresuryana.metembangbali.utils.event.TembangDetailEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MetembangRepository
) : ViewModel() {

    private val _tembang = MutableLiveData<TembangDetailEvent>()
    val tembang: LiveData<TembangDetailEvent> = _tembang

    fun getTembangDetail(tembangUID: String) {
        viewModelScope.launch {
            _tembang.value = TembangDetailEvent.Loading
            when (val response = repository.getTembangDetail(tembangUID)) {
                is Resource.Success ->
                    _tembang.value = TembangDetailEvent.Success(response.data)
                is Resource.Error ->
                    _tembang.value = TembangDetailEvent.Error(response.message!!)
                is Resource.NetworkError ->
                    _tembang.value = TembangDetailEvent.NetworkError
            }
        }
    }

    fun getNextTembang() {
        viewModelScope.launch {
            _tembang.value = TembangDetailEvent.Loading
            when (val response = repository.getRandomTembang()) {
                is Resource.Success -> {
                    when (val res = repository.getTembangDetail(response.data)) {
                        is Resource.Success ->
                            _tembang.value = TembangDetailEvent.Success(res.data)
                        is Resource.Error ->
                            _tembang.value = TembangDetailEvent.Error(res.message!!)
                        is Resource.NetworkError ->
                            _tembang.value = TembangDetailEvent.NetworkError
                    }
                }
                is Resource.Error ->
                    _tembang.value = TembangDetailEvent.Error(response.message!!)
                is Resource.NetworkError ->
                    _tembang.value = TembangDetailEvent.NetworkError
            }
        }
    }
}