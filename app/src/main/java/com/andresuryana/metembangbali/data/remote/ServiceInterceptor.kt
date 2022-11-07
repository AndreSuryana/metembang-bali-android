package com.andresuryana.metembangbali.data.remote

import android.content.Context
import com.andresuryana.metembangbali.utils.SessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ServiceInterceptor @Inject constructor(
    @ApplicationContext context: Context
) : Interceptor {

    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        // Create new request builder
        val requestBuilder = chain.request().newBuilder()

        // Add content-type header
        requestBuilder.addHeader("Accept", "application/json")

        // Get firebase auth token from session manager
        sessionManager.fetchAuthToken()?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}