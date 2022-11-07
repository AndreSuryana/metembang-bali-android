package com.andresuryana.metembangbali.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class User(

	@SerializedName("id")
	val id: Int? = null,

	@SerializedName("name")
	val name: String? = null,

	@SerializedName("email")
	val email: String? = null,

	@SerializedName("phone")
	val phone: String? = null,

	@SerializedName("occupation")
	val occupation: String? = null,

	@SerializedName("address")
	val address: String? = null,

	@SerializedName("photo_url")
	val photoUrl: String? = null,

	@SerializedName("updated_at")
	val updatedAt: Date? = null,

	@SerializedName("created_at")
	val createdAt: Date? = null

) : Parcelable