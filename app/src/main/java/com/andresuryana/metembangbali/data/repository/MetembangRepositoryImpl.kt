package com.andresuryana.metembangbali.data.repository

import com.andresuryana.metembangbali.data.model.*
import com.andresuryana.metembangbali.data.remote.MetembangService
import com.andresuryana.metembangbali.utils.Resource
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import javax.inject.Inject

class MetembangRepositoryImpl @Inject constructor(
    private val service: MetembangService
) : MetembangRepository {

    private fun <T>handleError(t: Throwable): Resource<T> {
        return when (t) {
            is IOException -> Resource.NetworkError
            is HttpException -> Resource.Error(t.message())
            else -> Resource.Error("An error occurred")
        }
    }

    private fun parseErrorBody(errorBody: ResponseBody?): Resource.Error {
        val errorBodyString = errorBody?.string().toString()
        val errorResponse = Gson().fromJson(errorBodyString, Wrapper::class.java)
        return Resource.Error(errorResponse.message)
    }

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
            handleError(t)
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
            handleError(t)
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
            handleError(t)
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
            handleError(t)
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
            handleError(t)
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
            handleError(t)
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
            handleError(t)
        }
    }

    override suspend fun createSubmission(
        title: String?,
        category: Category?,
        subCategory: SubCategory?,
        lyrics: ArrayList<String>?,
        usages: ArrayList<Usage>?,
        mood: Mood?,
        rule: Rule?,
        meaning: String?,
        lyricsIDN: ArrayList<String>?,
        coverImageFile: File?,
        coverSource: String?,
        audioFile: File?
    ): Resource<Submission> {

        // Media content type
        val contentType = "text/plain".toMediaType()

        // Create request body
        val requestBody = HashMap<String?, RequestBody?>()

        // General data request body
        requestBody["title"] = title?.toRequestBody(contentType)
        requestBody["category"] = category?.id?.toRequestBody(contentType)
        if (subCategory != null) requestBody["sub_category"] =
            subCategory.id.toRequestBody(contentType)
        requestBody["lyrics"] = Gson().toJson(lyrics).toRequestBody(contentType)

        // Additional data request body
        if (usages?.isNotEmpty() == true) {
            // Create usages json array
            val usagesJsonArray = ArrayList<HashMap<String, String>>()
            usages.forEach {
                val usageJson = HashMap<String, String>()
                usageJson["type"] = it.typeId.toString()
                usageJson["activity"] = it.activity.toString()
                usagesJsonArray.add(usageJson)
            }

            requestBody["usages"] = Gson().toJson(usagesJsonArray).toRequestBody(contentType)
        }

        if (mood != null)
            requestBody["mood"] = mood.id.toRequestBody(contentType)

        if (rule != null) {
            // Create rule json
            val ruleJson = HashMap<String, String>()
            ruleJson["name"] = rule.id ?: title.toString()
            ruleJson["guru_dingdong"] = rule.guruDingdong
            ruleJson["guru_wilang"] = rule.guruWilang
            ruleJson["guru_gatra"] = rule.guruGatra.toString()

            requestBody["rule"] = Gson().toJson(ruleJson).toRequestBody(contentType)
        }

        if (meaning != null)
            requestBody["meaning"] = meaning.toRequestBody(contentType)

        if (lyricsIDN?.isNotEmpty() == true)
            requestBody["lyrics_idn"] = Gson().toJson(lyricsIDN).toRequestBody(contentType)

        // Media data request body
        if (coverImageFile != null)
            requestBody["cover\"; filename=\"${coverImageFile.name}\" "] =
                coverImageFile.asRequestBody(contentType)

        if (coverSource != null)
            requestBody["cover_source"] = coverSource.toRequestBody(contentType)

        if (audioFile != null)
            requestBody["audio\"; filename=\"${audioFile.name}\" "] =
                audioFile.asRequestBody(contentType)


        val response = service.createSubmission(requestBody)
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
            handleError(t)
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
            handleError(t)
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
            handleError(t)
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
            handleError(t)
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
            handleError(t)
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
            handleError(t)
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
            handleError(t)
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
            handleError(t)
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
            handleError(t)
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
            handleError(t)
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
            handleError(t)
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
            handleError(t)
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
            handleError(t)
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
            handleError(t)
        }
    }
}