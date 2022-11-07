package com.andresuryana.metembangbali.utils.event

import com.andresuryana.metembangbali.data.model.Rule

sealed class RuleEvent {
    class Success(val rules: ArrayList<Rule>) : RuleEvent()
    class Error(val message: String) : RuleEvent()
    object NetworkError : RuleEvent()
    object Loading : RuleEvent()
    object Empty : RuleEvent()
}
