package com.example.newsapp.api

import NewsAPIService
import com.example.newsapp.constants.Constants
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIInstance {
    private val retrofit by lazy{
        Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val original = chain.request()
                        val requestBuilder = original.newBuilder()
                            .header("Authorization", "Bearer ${Constants.API_KEY}")
                        val request = requestBuilder.build()
                        chain.proceed(request)
                    }
                    .build()
            )
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }


    val api:NewsAPIService by lazy{
        retrofit.create(NewsAPIService::class.java)
    }
}