package com.andresuryana.metembangbali.di

import android.content.Context
import com.andresuryana.metembangbali.BuildConfig
import com.andresuryana.metembangbali.data.remote.MetembangService
import com.andresuryana.metembangbali.data.remote.NetworkInterceptor
import com.andresuryana.metembangbali.data.remote.ServiceInterceptor
import com.andresuryana.metembangbali.data.repository.MetembangRepository
import com.andresuryana.metembangbali.data.repository.MetembangRepositoryImpl
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MetembangModule {

    @Provides
    @Singleton
    fun provideMetembangService(
        @ApplicationContext context: Context
    ): MetembangService {
        // Interceptors
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        )
        val networkInterceptor = NetworkInterceptor(context)
        val serviceInterceptor = ServiceInterceptor(context)

        // Client
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(networkInterceptor)
            .addInterceptor(serviceInterceptor)
            .build()

        // Gson Converter Factory
        val gsonConverterFactory = GsonConverterFactory.create(
            GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .setLenient()
                .create()
        )

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(client)
            .addConverterFactory(gsonConverterFactory)
            .build()
            .create(MetembangService::class.java)
    }

    @Provides
    @Singleton
    fun provideMetembangRepository(service: MetembangService): MetembangRepository =
        MetembangRepositoryImpl(service)
}