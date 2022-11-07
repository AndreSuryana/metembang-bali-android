package com.andresuryana.metembangbali.ui.main.profile.submission

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresuryana.metembangbali.data.repository.MetembangRepository
import com.andresuryana.metembangbali.utils.Resource
import com.andresuryana.metembangbali.utils.event.DeleteSubmissionEvent
import com.andresuryana.metembangbali.utils.event.SubmissionListEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSubmissionViewModel @Inject constructor(
    private val repository: MetembangRepository
) : ViewModel() {

    private val _submission = MutableLiveData<SubmissionListEvent>()
    val submission: LiveData<SubmissionListEvent> = _submission

    private val _deleteSubmission = MutableSharedFlow<DeleteSubmissionEvent>()
    val deleteSubmission: SharedFlow<DeleteSubmissionEvent> = _deleteSubmission

    fun getUserSubmission() {
        viewModelScope.launch {
            _submission.value = SubmissionListEvent.Loading
            when (val response = repository.userSubmissions()) {
                is Resource.Success ->
                    _submission.value = SubmissionListEvent.Success(response.data)
                is Resource.Error ->
                    _submission.value = SubmissionListEvent.Error(response.message!!)
                is Resource.NetworkError ->
                    _submission.value = SubmissionListEvent.NetworkError
            }
        }
    }

    fun deleteUserSubmission(id: Int) {
        viewModelScope.launch {
            _deleteSubmission.emit(DeleteSubmissionEvent.Loading)
            when (val response = repository.deleteUserSubmission(id)) {
                is Resource.Success ->
                    _deleteSubmission.emit(DeleteSubmissionEvent.Success(response.data))
                is Resource.Error ->
                    _deleteSubmission.emit(DeleteSubmissionEvent.Error(response.message!!))
                is Resource.NetworkError ->
                    _deleteSubmission.emit(DeleteSubmissionEvent.NetworkError)
            }
        }
    }
}