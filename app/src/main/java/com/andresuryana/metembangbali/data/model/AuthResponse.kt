package com.andresuryana.metembangbali.data.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(

	@SerializedName("access_token")
	val token: String,

	@SerializedName("user")
	val user: User
)