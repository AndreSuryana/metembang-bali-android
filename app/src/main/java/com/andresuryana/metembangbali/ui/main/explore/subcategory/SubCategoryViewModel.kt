package com.andresuryana.metembangbali.ui.main.explore.subcategory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.metembangbali.data.repository.MetembangRepository
import com.andresuryana.metembangbali.utils.Resource
import com.andresuryana.metembangbali.utils.event.SubCategoryEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubCategoryViewModel @Inject constructor(
    private val repository: MetembangRepository
) : ViewModel() {

    private val _subCategories = MutableLiveData<SubCategoryEvent>()
    val subCategories: LiveData<SubCategoryEvent> = _subCategories

    fun getSubCategories(id: String?) {
        viewModelScope.launch {
            _subCategories.value = SubCategoryEvent.Loading
            if (!id.isNullOrBlank()) {
                when (val response = repository.getSubCategories(id)) {
                    is Resource.Success -> {
                        if (response.data.isEmpty()) _subCategories.value = SubCategoryEvent.Empty
                        else _subCategories.value = SubCategoryEvent.Success(response.data)
                    }
                    is Resource.Error ->
                        _subCategories.value = SubCategoryEvent.Error(response.message!!)
                    is Resource.NetworkError ->
                        _subCategories.value = SubCategoryEvent.NetworkError
                }
            } else {
                _subCategories.value = SubCategoryEvent.Error("Category need to be specified!")
            }
        }
    }
}