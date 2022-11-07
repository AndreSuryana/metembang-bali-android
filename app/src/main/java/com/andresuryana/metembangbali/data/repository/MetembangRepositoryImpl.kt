package com.andresuryana.metembangbali.data.repository

import android.util.Log
import com.andresuryana.metembangbali.data.model.*
import com.andresuryana.metembangbali.data.remote.MetembangService
import com.andresuryana.metembangbali.utils.Resource
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import javax.inject.Inject

class MetembangRepositoryImpl @Inject constructor(
    private val service: MetembangService
) : MetembangRepository {

    override suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Resource<AuthResponse> {

        val response = service.signUp(name, email, password)
        val result = response.body()

        return try {
            if (response.isSuccessful && result?.data != null)
                Resource.Success(result.data)
            else {
                val errorBody = response.errorBody()?.string().toString()
                val errorResponse = Gson().fromJson(errorBody, Wrapper::class.java)
                Resource.Error(errorResponse.message)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.Error(t.message())
                else -> Resource.Error()
            }
        }
    }

    override suspend fun signIn(email: String, password: String): Resource<AuthResponse> {

        val response = service.signIn(email, password)
        val result = response.body()

        return try {
            if (response.isSuccessful && result?.data != null)
                Resource.Success(result.data)
            else {
                val errorBody = response.errorBody()?.string().toString()
                val errorResponse = Gson().fromJson(errorBody, Wrapper::class.java)
                Resource.Error(errorResponse.message)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.Error(t.message())
                else -> Resource.Error()
            }
        }
    }

    override suspend fun signOut(): Resource<Boolean> {

        val response = service.signOut()
        val result = response.body()

        return try {
            if (response.isSuccessful && result?.data != null)
                Resource.Success(result.data)
            else {
                val errorBody = response.errorBody()?.string().toString()
                val errorResponse = Gson().fromJson(errorBody, Wrapper::class.java)
                Resource.Error(errorResponse.message)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.Error(t.message())
                else -> Resource.Error()
            }
        }
    }

    override suspend fun fetchUser(): Resource<User> {

        val response = service.fetchUser()
        val result = response.body()

        return try {
            if (response.isSuccessful && result?.data != null)
                Resource.Success(result.data)
            else {
                val errorBody = response.errorBody()?.string().toString()
                val errorResponse = Gson().fromJson(errorBody, Wrapper::class.java)
                Resource.Error(errorResponse.message)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.Error(t.message())
                else -> Resource.Error()
            }
        }
    }

    override suspend fun updateUser(
        name: String,
        email: String,
        phone: String,
        address: String,
        occupation: String?
    ): Resource<User> {

        val response = service.updateUser(name, email, phone, address, occupation)
        val result = response.body()

        return try {
            if (response.isSuccessful && result?.data != null)
                Resource.Success(result.data)
            else {
                val errorBody = response.errorBody()?.string().toString()
                val errorResponse = Gson().fromJson(errorBody, Wrapper::class.java)
                Resource.Error(errorResponse.message)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.Error(t.message())
                else -> Resource.Error()
            }
        }
    }

    override suspend fun uploadUserPhoto(photo: File): Resource<Boolean> {

        val photoRequestBody =
            MultipartBody.Part.createFormData("photo", photo.name, photo.asRequestBody())

        val response = service.uploadUserPhoto(photoRequestBody)
        val result = response.body()

        return try {
            if (response.isSuccessful && result?.data != null)
                Resource.Success(result.data)
            else {
                val errorBody = response.errorBody()?.string().toString()
                val errorResponse = Gson().fromJson(errorBody, Wrapper::class.java)
                Resource.Error(errorResponse.message)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.Error(t.message())
                else -> Resource.Error()
            }
        }
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Resource<Boolean> {

        val response = service.changePassword(oldPassword, newPassword, confirmPassword)
        val result = response.body()

        return try {
            if (response.isSuccessful && result?.data != null)
                Resource.Success(result.data)
            else {
                val errorBody = response.errorBody()?.string().toString()
                val errorResponse = Gson().fromJson(errorBody, Wrapper::class.java)
                Resource.Error(errorResponse.message)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.Error(t.message())
                else -> Resource.Error()
            }
        }
    }

    override suspend fun createSubmission(
        title: String?,
        category: Category?,
        subCategory: SubCategory?,
        lyrics: ArrayList<String>?,
        usageType: UsageType?,
        usages: ArrayList<Usage>?,
        mood: Mood?,
        rule: Rule?,
        meaning: String?,
        lyricsIDN: ArrayList<String>?,
        coverImageFile: File?,
        coverSource: String?,
        audioFile: File?
    ): Resource<Submission> {

        // TODO : Implement request body here or in view model?
        // Rule
        val ruleJson = HashMap<String, String>()
        if (rule != null) {
            ruleJson["name"] = rule.name ?: title.toString()
            ruleJson["guru_dingdong"] = rule.guruDingdong
            ruleJson["guru_wilang"] = rule.guruWilang
            ruleJson["guru_gatra"] = rule.guruGatra.toString()
        }

        val body = JSONObject().run {
            put("title", title)
            put("category", category?.id)
            put("subCategory", subCategory?.id)
            put("lyrics", lyrics)
            put("usage_type", usageType?.id)
            put("usage", usages)
            put("mood", mood?.id)
            put("rule", ruleJson)
            put("meaning", meaning)
            put("lyrics_idn", lyricsIDN)
            put("cover", coverImageFile?.asRequestBody())
            put("cover_source", coverSource)
            put("audio", audioFile?.asRequestBody())
            toString().toRequestBody()
        }

        Log.d("Repository", "body=$body")

        val response = service.createSubmission(body)
        val result = response.body()

        return try {
            if (response.isSuccessful && result?.data != null)
                Resource.Success(result.data)
            else {
                val errorBody = response.errorBody()?.string().toString()
                val errorResponse = Gson().fromJson(errorBody, Wrapper::class.java)
                Resource.Error(errorResponse.message)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.Error(t.message())
                else -> Resource.Error()
            }
        }
    }

    override suspend fun userSubmissions(): Resource<ListResponse<Submission>> {

        val response = service.userSubmissions()
        val result = response.body()

        return try {
            if (response.isSuccessful && result?.data != null)
                Resource.Success(result.data)
            else {
                val errorBody = response.errorBody()?.string().toString()
                val errorResponse = Gson().fromJson(errorBody, Wrapper::class.java)
                Resource.Error(errorResponse.message)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.Error(t.message())
                else -> Resource.Error()
            }
        }
    }

    override suspend fun deleteUserSubmission(id: Int): Resource<Boolean> {

        val response = service.deleteSubmission(id)
        val result = response.body()

        return try {
            if (response.isSuccessful && result?.data != null)
                Resource.Success(result.data)
            else {
                val errorBody = response.errorBody()?.string().toString()
                val errorResponse = Gson().fromJson(errorBody, Wrapper::class.java)
                Resource.Error(errorResponse.message)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.Error(t.message())
                else -> Resource.Error()
            }
        }
    }

    override suspend fun getCategories(): Resource<ArrayList<Category>> {

        val response = service.getCategories()
        val result = response.body()

        return try {
            if (response.isSuccessful && result?.data != null)
                Resource.Success(result.data)
            else {
                parseErrorBody(response.errorBody())
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.Error(t.message())
                else -> Resource.Error()
            }
        }
    }

    override suspend fun getSubCategories(id: String): Resource<ArrayList<SubCategory>> {

        val response = service.getSubCategories(id)
        val result = response.body()

        return try {
            if (response.isSuccessful && result?.data != null)
                Resource.Success(result.data)
            else {
                val errorBody = response.errorBody()?.string().toString()
                val errorResponse = Gson().fromJson(errorBody, Wrapper::class.java)
                Resource.Error(errorResponse.message)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.Error(t.message())
                else -> Resource.Error()
            }
        }
    }

    override suspend fun getTembang(
        category: String?,
        usageType: String?,
        usage: String?,
        rule: String?,
        mood: String?
    ): Resource<ListResponse<Tembang>> {

        val response = service.getTembang(category, usageType, usage, rule, mood)
        val result = response.body()

        return try {
            if (response.isSuccessful && result?.data != null)
                Resource.Success(result.data)
            else {
                val errorBody = response.errorBody()?.string().toString()
                val errorResponse = Gson().fromJson(errorBody, Wrapper::class.java)
                Resource.Error(errorResponse.message)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.Error(t.message())
                else -> Resource.Error()
            }
        }
    }

    override suspend fun latest(): Resource<ListResponse<Tembang>> {

        val response = service.latest()
        val result = response.body()

        return try {
            if (response.isSuccessful && result?.data != null)
                Resource.Success(result.data)
            else {
                val errorBody = response.errorBody()?.string().toString()
                val errorResponse = Gson().fromJson(errorBody, Wrapper::class.java)
                Resource.Error(errorResponse.message)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.Error(t.message())
                else -> Resource.Error()
            }
        }
    }

    override suspend fun topMostViewed(): Resource<ListResponse<Tembang>> {

        val response = service.topMostViewed()
        val result = response.body()

        return try {
            if (response.isSuccessful && result?.data != null)
                Resource.Success(result.data)
            else {
                val errorBody = response.errorBody()?.string().toString()
                val errorResponse = Gson().fromJson(errorBody, Wrapper::class.java)
                Resource.Error(errorResponse.message)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.Error(t.message())
                else -> Resource.Error()
            }
        }
    }

    override suspend fun getTembangDetail(tembangUID: String): Resource<Tembang> {

        val response = service.getTembangDetail(tembangUID)
        val result = response.body()

        return try {
            if (response.isSuccessful && result?.data != null)
                Resource.Success(result.data)
            else {
                val errorBody = response.errorBody()?.string().toString()
                val errorResponse = Gson().fromJson(errorBody, Wrapper::class.java)
                Resource.Error(errorResponse.message)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.Error(t.message())
                else -> Resource.Error()
            }
        }
    }

    override suspend fun getFilterRule(): Resource<ArrayList<Rule>> {

        val response = service.getFilterRule()
        val result = response.body()

        return try {
            if (response.isSuccessful && result?.data != null)
                Resource.Success(result.data)
            else {
                val errorBody = response.errorBody()?.string().toString()
                val errorResponse = Gson().fromJson(errorBody, Wrapper::class.java)
                Resource.Error(errorResponse.message)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.Error(t.message())
                else -> Resource.Error()
            }
        }
    }

    override suspend fun getFilterUsage(type: String?): Resource<ArrayList<Usage>> {

        val response = service.getFilterUsage(type)
        val result = response.body()

        return try {
            if (response.isSuccessful && result?.data != null)
                Resource.Success(result.data)
            else {
                val errorBody = response.errorBody()?.string().toString()
                val errorResponse = Gson().fromJson(errorBody, Wrapper::class.java)
                Resource.Error(errorResponse.message)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.Error(t.message())
                else -> Resource.Error()
            }
        }
    }

    override suspend fun getFilterUsageType(): Resource<ArrayList<UsageType>> {

        val response = service.getFilterUsageType()
        val result = response.body()

        return try {
            if (response.isSuccessful && result?.data != null)
                Resource.Success(result.data)
            else {
                val errorBody = response.errorBody()?.string().toString()
                val errorResponse = Gson().fromJson(errorBody, Wrapper::class.java)
                Resource.Error(errorResponse.message)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.Error(t.message())
                else -> Resource.Error()
            }
        }
    }

    override suspend fun getFilterMood(): Resource<ArrayList<Mood>> {

        val response = service.getFilterMood()
        val result = response.body()

        return try {
            if (response.isSuccessful && result?.data != null)
                Resource.Success(result.data)
            else {
                val errorBody = response.errorBody()?.string().toString()
                val errorResponse = Gson().fromJson(errorBody, Wrapper::class.java)
                Resource.Error(errorResponse.message)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.Error(t.message())
                else -> Resource.Error()
            }
        }
    }

    override suspend fun getRandomTembang(): Resource<String> {

        val response = service.getRandomTembang()
        val result = response.body()

        return try {
            if (response.isSuccessful && result?.data != null)
                Resource.Success(result.data)
            else {
                parseErrorBody(response.errorBody())
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError
                is HttpException -> Resource.Error(t.message())
                else -> Resource.Error()
            }
        }
    }

    private fun parseErrorBody(errorBody: ResponseBody?): Resource.Error {

        val errorBodyString = errorBody?.string().toString()
        val errorResponse = Gson().fromJson(errorBodyString, Wrapper::class.java)

        return Resource.Error(errorResponse.message)
    }
}