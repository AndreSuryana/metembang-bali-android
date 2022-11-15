package com.andresuryana.metembangbali.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rule(

    @SerializedName("id")
    val id: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("guru_gatra")
    val guruGatra: Int,

    @SerializedName("guru_dingdong")
    val guruDingdong: String,

    @SerializedName("guru_wilang")
    val guruWilang: String,

    @SerializedName("tembang_submission_id")
    val tembangSubmissionId: Int? = null

) : Parcelable {

    override fun toString(): String {

        val arrayGuruWilang = guruWilang.split(", ")
        val arrayGuruDingdong = guruDingdong.split(", ")

        var result = ""

        for (i in 0 until guruGatra) {
            result = if (i == guruGatra - 1) result + arrayGuruWilang[i] + arrayGuruDingdong[i]
            else result + arrayGuruWilang[i] + arrayGuruDingdong[i] + ", "
        }

        return result
    }
}