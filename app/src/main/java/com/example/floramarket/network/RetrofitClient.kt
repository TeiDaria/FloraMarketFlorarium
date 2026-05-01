package com.example.floramarket.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://router.huggingface.co/"

    // OkHttpClient с увеличенными таймаутами
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)  // Таймаут на подключение
        .readTimeout(60, TimeUnit.SECONDS)     // Таймаут на чтение ответа
        .writeTimeout(30, TimeUnit.SECONDS)    // Таймаут на запись
        .build()

    val instance: HuggingFaceApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HuggingFaceApi::class.java)
    }
}