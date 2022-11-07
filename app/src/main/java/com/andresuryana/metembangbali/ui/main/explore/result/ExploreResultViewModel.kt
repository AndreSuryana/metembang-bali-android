package com.andresuryana.metembangbali.ui.main.explore.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.metembangbali.data.repository.MetembangRepository
import com.andresuryana.metembangbali.utils.Resource
import com.andresuryana.metembangbali.utils.event.ExploreResultEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreResultViewModel @Inject constructor(
    private val repository: MetembangRepository
) : ViewModel() {

    private val _listTembang = MutableLiveData<ExploreResultEvent>()
    val listTembang: LiveData<ExploreResultEvent> = _listTembang

    fun getTembang(categoryId: String?) {
        viewModelScope.launch {
            if (!categoryId.isNullOrBlank()) {
                _listTembang.value = ExploreResultEvent.Loading
                when (val response = repository.getTembang(category = categoryId)) {
                    is Resource.Success -> {
                        if (response.data.size == 0) _listTembang.value = ExploreResultEvent.Empty
                        else _listTembang.value = ExploreResultEvent.Success(response.data)
                    }
                    is Resource.Error ->
                        _listTembang.value = ExploreResultEvent.Error(response.message!!)
                    is Resource.NetworkError ->
                        _listTembang.value = ExploreResultEvent.NetworkError
                }
            } else {
                _listTembang.value = ExploreResultEvent.Error("Category id need to be specified!")
            }
        }
    }
}