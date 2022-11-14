package com.andresuryana.metembangbali.ui.add

import androidx.lifecycle.*
import com.andresuryana.metembangbali.adapter.viewpager.AddSubmissionViewPagerAdapter.Companion.GENERAL
import com.andresuryana.metembangbali.data.model.*
import com.andresuryana.metembangbali.data.repository.MetembangRepository
import com.andresuryana.metembangbali.utils.Resource
import com.andresuryana.metembangbali.utils.event.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddSubmissionViewModel @Inject constructor(
    private val repository: MetembangRepository
) : ViewModel() {

    /**
     * Create submission event
     */
    private val _submission = MutableSharedFlow<SubmissionEvent>()
    val submission: SharedFlow<SubmissionEvent> = _submission

    /**
     * Current page state
     */
    private val _currentPage = MutableLiveData(GENERAL)
    val currentPage: LiveData<Int> = _currentPage


    /**
     * Dropdown variables
     */
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


    /**
     * General, additional & media data variables
     */
    var title: String? = null
    var category: Category? = null
    var subCategory: SubCategory? = null
    var lyrics: ArrayList<String>? = null
    var usageType: UsageType? = null
    var hasUsages: ArrayList<Usage>? = null
    var mood: Mood? = null
    var rule: Rule? = null
    var meaning: String? = null
    var lyricsIDN: ArrayList<String>? = null
    var coverImageFile: File? = null
    var coverSource: String? = null
    var audioFile: File? = null


    /**
     * Create submission
     */
    fun createSubmission() {
        viewModelScope.launch {
            _submission.emit(SubmissionEvent.Loading)
            val response = repository.createSubmission(
                title,
                category,
                subCategory,
                lyrics,
                usageType,
                hasUsages,
                mood,
                rule,
                meaning,
                lyricsIDN,
                coverImageFile,
                coverSource,
                audioFile
            )
            when (response) {
                is Resource.Success ->
                    _submission.emit(SubmissionEvent.Success(response.data))
                is Resource.Error ->
                    _submission.emit(SubmissionEvent.Error(response.message!!))
                is Resource.NetworkError ->
                    _submission.emit(SubmissionEvent.NetworkError)
            }
        }
    }


    /**
     * Current page setter
     */
    fun setPagePosition(position: Int) {
        _currentPage.value = position
    }


    /**
     * Dropdown getter
     */
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