package com.andresuryana.metembangbali.data.model

import com.google.gson.annotations.SerializedName

data class Submission(

    @SerializedName("id")
	val id: Int,

    @SerializedName("uid")
	val uid: String,

    @SerializedName("title")
	val title: String,

    @SerializedName("category")
	val category: String,

    @SerializedName("sub_category")
	val subCategory: String? = null,

    @SerializedName("lyrics")
	val lyrics: ArrayList<String>? = null,

    @SerializedName("lyrics_idn")
	val lyricsIDN: ArrayList<String>? = null,

    @SerializedName("meaning")
	val meaning: String? = null,

    @SerializedName("cover_url")
    val coverUrl: String? = null,

    @SerializedName("cover_source")
    val coverSource: String? = null,

    @SerializedName("audio_url")
    val audioUrl: String? = null,

    @SerializedName("rule")
	val rule: Rule? = null,

    @SerializedName("usages")
	val usages: ArrayList<Usage>? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("created_at")
	val createdAt: String? = null,

    @SerializedName("updated_at")
	val updatedAt: String? = null
)