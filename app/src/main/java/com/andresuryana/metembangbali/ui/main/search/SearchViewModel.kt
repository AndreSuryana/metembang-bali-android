package com.andresuryana.metembangbali.ui.main.search

import androidx.lifecycle.*
import com.andresuryana.metembangbali.data.model.SearchFilter
import com.andresuryana.metembangbali.data.model.Tembang
import com.andresuryana.metembangbali.data.repository.MetembangRepository
import com.andresuryana.metembangbali.utils.Resource
import com.andresuryana.metembangbali.utils.SortMethod
import com.andresuryana.metembangbali.utils.event.TembangListEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MetembangRepository
) : ViewModel() {

    private val _listTembang = MutableLiveData<TembangListEvent>()
    val listTembang: LiveData<TembangListEvent> = _listTembang

    private val _list = MutableLiveData<ArrayList<Tembang>>()
    val list: LiveData<ArrayList<Tembang>> = _list

    private val _filter = MutableStateFlow<SearchFilter?>(null)

    private val _sort = MutableStateFlow<SortMethod?>(null)

    fun getTembang() {
        viewModelScope.launch {
            _filter.collectLatest { filter ->
                _sort.collect { sort ->
                    if (filter != null) {
                        _listTembang.value = TembangListEvent.Loading
                        when (val response = repository.getTembang(
                            filter.subCategory?.id ?: filter.category?.id,
                            filter.usageType?.id,
                            filter.usage?.id,
                            filter.rule?.id,
                            filter.mood?.id,
                            sort
                        )) {
                            is Resource.Success -> {
                                if (response.data.size == 0) _listTembang.value =
                                    TembangListEvent.Empty
                                else {
                                    _listTembang.value = TembangListEvent.Success(response.data)
                                    _list.value = response.data.list
                                }
                            }
                            is Resource.Error ->
                                _listTembang.value = TembangListEvent.Error(response.message!!)
                            is Resource.NetworkError ->
                                _listTembang.value = TembangListEvent.NetworkError
                        }
                    } else {
                        _list.value = arrayListOf()
                    }
                }
            }
        }
    }

    fun setFilter(filter: SearchFilter?) {
        viewModelScope.launch {
            _filter.emit(filter)
            getTembang()
        }
    }

    fun setSortingMethod(sort: SortMethod) {
        viewModelScope.launch {
            _sort.emit(sort)
            getTembang()
        }
    }
}