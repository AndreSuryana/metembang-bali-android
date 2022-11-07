package com.andresuryana.metembangbali.ui.main.search.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.metembangbali.data.model.*
import com.andresuryana.metembangbali.data.repository.MetembangRepository
import com.andresuryana.metembangbali.utils.Resource
import com.andresuryana.metembangbali.utils.event.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val repository: MetembangRepository
) : ViewModel() {

    private val _categories = MutableLiveData<CategoryEvent>()
    val categories: LiveData<CategoryEvent> = _categories

    private val _subCategories = MutableLiveData<SubCategoryEvent>()
    val subCategories: LiveData<SubCategoryEvent> = _subCategories

    private val _usageTypes = MutableLiveData<UsageTypeEvent>()
    val usageTypes: LiveData<UsageTypeEvent> = _usageTypes

    private val _usages = MutableLiveData<UsageEvent>()
    val usages: LiveData<UsageEvent> = _usages

    private val _moods = MutableLiveData<MoodEvent>()
    val moods: LiveData<MoodEvent> = _moods

    private val _rules = MutableLiveData<RuleEvent>()
    val rules: LiveData<RuleEvent> = _rules

    var category: Category? = null
    var subCategory: SubCategory? = null
    var usageType: UsageType? = null
    var usage: Usage? = null
    var mood: Mood? = null
    var rule: Rule? = null

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

    fun getUsageTypes() {
        viewModelScope.launch {
            _usageTypes.value = UsageTypeEvent.Loading
            when (val response = repository.getFilterUsageType()) {
                is Resource.Success -> {
                    if (response.data.isEmpty()) _usageTypes.value = UsageTypeEvent.Empty
                    else _usageTypes.value = UsageTypeEvent.Success(response.data)
                }
                is Resource.Error ->
                    _usageTypes.value = UsageTypeEvent.Error(response.message!!)
                is Resource.NetworkError ->
                    _usageTypes.value = UsageTypeEvent.NetworkError
            }
        }
    }

    fun getUsages(type: String?) {
        viewModelScope.launch {
            _usages.value = UsageEvent.Loading
            when (val response = repository.getFilterUsage(type)) {
                is Resource.Success -> {
                    if (response.data.isEmpty()) _usages.value = UsageEvent.Empty
                    else _usages.value = UsageEvent.Success(response.data)
                }
                is Resource.Error ->
                    _usages.value = UsageEvent.Error(response.message!!)
                is Resource.NetworkError ->
                    _usages.value = UsageEvent.NetworkError
            }
        }
    }

    fun getMoods() {
        viewModelScope.launch {
            _moods.value = MoodEvent.Loading
            when (val response = repository.getFilterMood()) {
                is Resource.Success -> {
                    if (response.data.isEmpty()) _moods.value = MoodEvent.Empty
                    else _moods.value = MoodEvent.Success(response.data)
                }
                is Resource.Error ->
                    _moods.value = MoodEvent.Error(response.message!!)
                is Resource.NetworkError ->
                    _moods.value = MoodEvent.NetworkError
            }
        }
    }

    fun getRules() {
        viewModelScope.launch {
            _rules.value = RuleEvent.Loading
            when (val response = repository.getFilterRule()) {
                is Resource.Success -> {
                    if (response.data.isEmpty()) _rules.value = RuleEvent.Empty
                    else _rules.value = RuleEvent.Success(response.data)
                }
                is Resource.Error ->
                    _rules.value = RuleEvent.Error(response.message!!)
                is Resource.NetworkError ->
                    _rules.value = RuleEvent.NetworkError
            }
        }
    }
}