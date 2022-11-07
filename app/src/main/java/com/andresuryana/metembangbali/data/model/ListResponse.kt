package com.andresuryana.metembangbali.data.model

import com.google.gson.annotations.SerializedName

data class ListResponse<T>(

    @SerializedName("size")
    val size: Int,

    @SerializedName("list")
    val list: ArrayList<T>,

    @SerializedName("query")
    val query: String?

)
