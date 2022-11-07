package com.andresuryana.metembangbali.utils.event

import com.andresuryana.metembangbali.data.model.Mood

sealed class MoodEvent {
    class Success(val moods: ArrayList<Mood>) : MoodEvent()
    class Error(val message: String) : MoodEvent()
    object NetworkError : MoodEvent()
    object Loading : MoodEvent()
    object Empty : MoodEvent()
}
