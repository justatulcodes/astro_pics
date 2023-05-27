package com.expeknow.nasabrowser.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object API_APOD {

    private val BASE_URL = "https://api.nasa.gov/planetary/"
    const val API_KEY = "3EmuuZMwHjcjyMUzc9rFWfCisCpdJrkifFUHlumj"

    //Moshi converts our JSON reponses into kotlin objects
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    //Helps to make network calls
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    //telling retrofit where we want to send our calls to build URLs
    val apod : APODService by lazy {
        retrofit.create(APODService::class.java)
    }


}