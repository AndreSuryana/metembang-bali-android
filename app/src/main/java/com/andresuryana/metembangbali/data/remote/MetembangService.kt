package com.andresuryana.metembangbali.data.remote

import com.andresuryana.metembangbali.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import kotlin.collections.ArrayList

interface MetembangService {

    /**
     * Authentication register user
     */
    @FormUrlEncoded
    @POST("v1/user/register")
    suspend fun signUp(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<Wrapper<AuthResponse>>

    /**
     * Authentication login user
     */
    @FormUrlEncoded
    @POST("v1/user/login")
    suspend fun signIn(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<Wrapper<AuthResponse>>

    /**
     * Authentication logout user
     */
    @POST("v1/user/logout")
    suspend fun signOut(): Response<Wrapper<Boolean>>

    /**
     * Fetch user information
     */
    @GET("v1/user")
    suspend fun fetchUser(): Response<Wrapper<User>>

    /**
     * Update user information
     */
    @FormUrlEncoded
    @PUT("v1/user")
    suspend fun updateUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("address") address: String,
        @Field("occupation") occupation: String? = null
    ): Response<Wrapper<User>>

    /**
     * Upload user profile photo
     */
    @Multipart
    @POST("v1/user/photo")
    suspend fun uploadUserPhoto(
        @Part photo: MultipartBody.Part
    ): Response<Wrapper<Boolean>>

    /**
     * Change user password
     */
    @FormUrlEncoded
    @POST("v1/user/password")
    suspend fun changePassword(
        @Field("password_old") oldPassword: String,
        @Field("password") newPassword: String,
        @Field("password_confirmation") confirmPassword: String
    ): Response<Wrapper<Boolean>>

    /**
     * Create submission
     */
    @Multipart
    @POST("v1/user/submission")
    suspend fun createSubmission(
        @PartMap body: HashMap<String?, RequestBody?>
    ): Response<Wrapper<Submission>>

    /**
     * Get all user submission
     */
    @GET("v1/user/submission")
    suspend fun userSubmissions(): Response<Wrapper<ListResponse<Submission>>>

    /**
     * Delete user submission
     */
    @DELETE("v1/user/submission/{id}/delete")
    suspend fun deleteSubmission(
        @Path("id") id: Int
    ): Response<Wrapper<Boolean>>

    /**
     * Get all categories
     */
    @GET("v1/tembang/category")
    suspend fun getCategories(): Response<Wrapper<ArrayList<Category>>>

    /**
     * Get all sub-categories from specified category
     */
    @GET("v1/tembang/sub-category")
    suspend fun getSubCategories(
        @Query("name") name: String
    ): Response<Wrapper<ArrayList<SubCategory>>>

    /**
     * Get all tembang with queries
     */
    @GET("v1/tembang")
    suspend fun getTembang(
        @Query("category") category: String? = null,
        @Query("usage_type") usageType: String? = null,
        @Query("usage") usage: String? = null,
        @Query("rule") rule: String? = null,
        @Query("mood") mood: String? = null,
        @Query("sort") sort: String? = null
    ): Response<Wrapper<ListResponse<Tembang>>>

    /**
     * Get latest tembang
     */
    @GET("v1/tembang/latest")
    suspend fun latest(): Response<Wrapper<ListResponse<Tembang>>>

    /**
     * Get top most viewed tembang
     */
    @GET("v1/tembang/top")
    suspend fun topMostViewed(): Response<Wrapper<ListResponse<Tembang>>>

    /**
     * Get tembang detail
     */
    @GET("v1/tembang/{tembangUID}/detail")
    suspend fun getTembangDetail(
        @Path("tembangUID") tembangUID: String
    ): Response<Wrapper<Tembang>>

    /**
     * Get filter rule
     */
    @GET("v1/tembang/filter/rule")
    suspend fun getFilterRule(): Response<Wrapper<ArrayList<Rule>>>

    /**
     * Get filter usage
     */
    @GET("v1/tembang/filter/usage")
    suspend fun getFilterUsage(@Query("type") type: String?): Response<Wrapper<ArrayList<Usage>>>

    /**
     * Get filter usage type
     */
    @GET("v1/tembang/filter/usage/type")
    suspend fun getFilterUsageType(): Response<Wrapper<ArrayList<UsageType>>>

    /**
     * Get filter mood
     */
    @GET("v1/tembang/filter/mood")
    suspend fun getFilterMood(): Response<Wrapper<ArrayList<Mood>>>

    /**
     * Get random tembang uid
     */
    @GET("v1/tembang/random")
    suspend fun getRandomTembang(): Response<Wrapper<String>>
}