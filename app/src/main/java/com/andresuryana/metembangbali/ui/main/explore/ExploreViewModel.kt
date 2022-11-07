package com.andresuryana.metembangbali.ui.main.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.metembangbali.data.repository.MetembangRepository
import com.andresuryana.metembangbali.utils.Resource
import com.andresuryana.metembangbali.utils.event.CategoryEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val repository: MetembangRepository
) : ViewModel() {

    private val _categories = MutableLiveData<CategoryEvent>()
    val categories: LiveData<CategoryEvent> = _categories

    fun getCategories() {
        viewModelScope.launch {
            _categories.value = CategoryEvent.Loading
            when (val response = repository.getCategories()) {
                is Resource.Success -> {
                    if (response.data.isEmpty()) _categories.value = CategoryEvent.Empty
                    else _categories.value = CategoryEvent.Success(response.data)
                }
                is Resource.Error ->
                    _categories.value = CategoryEvent.Error(response.message!!)
                is Resource.NetworkError ->
                    _categories.value = CategoryEvent.NetworkError
            }
        }
    }
}