package com.andresuryana.metembangbali.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Usage(

	@SerializedName("id")
	val id: String,

	@SerializedName("type_id")
	val typeId: String,

	@SerializedName("activity")
	val activity: String,

	@SerializedName("tembang_submission_id")
	val tembangSubmissionId: Int? = null

) : Parcelable {

	override fun toString(): String = activity
}