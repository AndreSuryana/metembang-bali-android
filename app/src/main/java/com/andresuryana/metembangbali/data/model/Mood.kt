package com.andresuryana.metembangbali.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Mood(

    @SerializedName("id")
    val id: String,

    @SerializedName("description")
    val description: String

) : Parcelable {

    override fun toString(): String = description
}
