package com.andresuryana.metembangbali.data.model

import com.google.gson.annotations.SerializedName

data class Wrapper<T>(

    @SerializedName("status")
    val status: String,

    @SerializedName("data")
    val data: T?,

    @SerializedName("message")
    val message: String?
)
