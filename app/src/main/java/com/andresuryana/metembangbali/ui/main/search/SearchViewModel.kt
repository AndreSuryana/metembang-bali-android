package com.andresuryana.metembangbali.ui.main.search

import androidx.lifecycle.*
import com.andresuryana.metembangbali.data.model.SearchFilter
import com.andresuryana.metembangbali.data.repository.MetembangRepository
import com.andresuryana.metembangbali.utils.Resource
import com.andresuryana.metembangbali.utils.event.TembangListEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MetembangRepository
) : ViewModel() {

    private val _listTembang = MutableLiveData<TembangListEvent>()
    val listTembang: LiveData<TembangListEvent> = _listTembang

    private val _filter = MutableLiveData<SearchFilter>()
    val filter: LiveData<SearchFilter> = _filter

    fun getTembang(filter: SearchFilter? = _filter.value) {
        _listTembang.value = TembangListEvent.Loading
        viewModelScope.launch {
            when (val response = repository.getTembang(
                filter?.subCategory?.id ?: filter?.category?.id,
                filter?.usageType?.id,
                filter?.usage?.id,
                filter?.rule?.id,
                filter?.mood?.id
            )) {
                is Resource.Success -> {
                    if (response.data.size == 0) _listTembang.value = TembangListEvent.Empty
                    else _listTembang.value = TembangListEvent.Success(response.data)
                }
                is Resource.Error ->
                    _listTembang.value = TembangListEvent.Error(response.message!!)
                is Resource.NetworkError ->
                    _listTembang.value = TembangListEvent.NetworkError
            }
        }
    }

    fun setFilter(filter: SearchFilter) {
        _filter.value = filter
    }
}