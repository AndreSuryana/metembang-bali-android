package com.andresuryana.metembangbali.data.remote

import android.content.Context
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.helper.NetworkHelper.isOnline
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import javax.inject.Inject

class NetworkInterceptor @Inject constructor(
    @ApplicationContext
    private val context: Context
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (isOnline(context)) {
            return Response.Builder()
                .code(12163)
                .request(chain.request())
                .protocol(Protocol.HTTP_2)
                .body("{\"data\": null, \"message\": \"${context.getString(R.string.error_no_internet_connection)}\"}".toResponseBody())
                .message(context.getString(R.string.error_no_internet_connection))
                .build()
        }

        return chain.proceed(chain.request())
    }
}