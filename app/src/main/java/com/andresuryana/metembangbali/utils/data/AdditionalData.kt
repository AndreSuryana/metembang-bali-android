package com.andresuryana.metembangbali.utils.data

import com.andresuryana.metembangbali.data.model.Mood
import com.andresuryana.metembangbali.data.model.Rule
import com.andresuryana.metembangbali.data.model.Usage
import com.andresuryana.metembangbali.data.model.UsageType

data class AdditionalData(

    val usageType: UsageType? = null,

    val usage: Usage? = null,

    val mood: Mood? = null,

    val rule: Rule? = null,

    val meaning: String? = null,

    val lyricsIDN: ArrayList<String>
)
