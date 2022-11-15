package com.andresuryana.metembangbali.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tembang(

    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("sub_category")
    val subCategory: String? = null,

    @SerializedName("lyrics")
    val lyrics: ArrayList<String>,

    @SerializedName("lyrics_idn")
    val lyricsIDN: ArrayList<String>? = null,

    @SerializedName("meaning")
    val meaning: String? = null,

    @SerializedName("cover_url")
    val coverUrl: String? = null,

    @SerializedName("audio_url")
    val audioUrl: String? = null,

    @SerializedName("rule")
    val rule: Rule? = null,

    @SerializedName("usages")
    val usage: ArrayList<Usage>? = null,

    @SerializedName("mood")
    val mood: Mood? = null,

    @SerializedName("view_count")
    val viewCount: Int? = null,

    @SerializedName("author")
    val author: String? = null,

    @SerializedName("cover_source")
    val coverSource: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null

) : Parcelable
