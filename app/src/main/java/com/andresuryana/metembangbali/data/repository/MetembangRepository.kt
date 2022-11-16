package com.andresuryana.metembangbali.data.repository

import com.andresuryana.metembangbali.data.model.*
import com.andresuryana.metembangbali.utils.Resource
import java.io.File
import kotlin.collections.ArrayList

interface MetembangRepository {

    /**
     * Authentication register user
     */
    suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Resource<AuthResponse>

    /**
     * Authentication login user
     */
    suspend fun signIn(
        email: String,
        password: String
    ): Resource<AuthResponse>

    /**
     * Authentication logout user
     */
    suspend fun signOut(): Resource<Boolean>

    /**
     * Fetch user information
     */
    suspend fun fetchUser(): Resource<User>

    /**
     * Update user information
     */
    suspend fun updateUser(
        name: String,
        email: String,
        phone: String,
        address: String,
        occupation: String? = null
    ): Resource<User>

    /**
     * Upload user profile photo
     */
    suspend fun uploadUserPhoto(photo: File): Resource<Boolean>

    /**
     * Change user password
     */
    suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Resource<Boolean>

    /**
     * Create submission
     */
    suspend fun createSubmission(
        title: String? = null,
        category: Category? = null,
        subCategory: SubCategory? = null,
        lyrics: ArrayList<String>? = null,
        usages: ArrayList<Usage>? = null,
        mood: Mood? = null,
        rule: Rule? = null,
        meaning: String? = null,
        lyricsIDN: ArrayList<String>? = null,
        coverImageFile: File? = null,
        coverSource: String? = null,
        audioFile: File? = null
    ): Resource<Submission>

    /**
     * Get all user submission
     */
    suspend fun userSubmissions(): Resource<ListResponse<Submission>>

    /**
     * Delete user submission
     */
    suspend fun deleteUserSubmission(id: Int): Resource<Boolean>

    /**
     * Get all categories
     */
    suspend fun getCategories(): Resource<ArrayList<Category>>

    /**
     * Get all sub-categories from specified category
     */
    suspend fun getSubCategories(id: String): Resource<ArrayList<SubCategory>>

    /**
     * Get all tembang with queries
     */
    suspend fun getTembang(
        category: String? = null,
        usageType: String? = null,
        usage: String? = null,
        rule: String? = null,
        mood: String? = null
    ): Resource<ListResponse<Tembang>>

    /**
     * Get latest tembang
     */
    suspend fun latest(): Resource<ListResponse<Tembang>>

    /**
     * Get top most viewed tembang
     */
    suspend fun topMostViewed(): Resource<ListResponse<Tembang>>

    /**
     * Get tembang detail
     */
    suspend fun getTembangDetail(
        tembangUID: String
    ): Resource<Tembang>

    /**
     * Get filter rule
     */
    suspend fun getFilterRule(): Resource<ArrayList<Rule>>

    /**
     * Get filter usage
     */
    suspend fun getFilterUsage(type: String? = null): Resource<ArrayList<Usage>>

    /**
     * Get filter usage type
     */
    suspend fun getFilterUsageType(): Resource<ArrayList<UsageType>>

    /**
     * Get filter mood
     */
    suspend fun getFilterMood(): Resource<ArrayList<Mood>>

    /**
     * Get random tembang uid
     */
    suspend fun getRandomTembang(): Resource<String>
}