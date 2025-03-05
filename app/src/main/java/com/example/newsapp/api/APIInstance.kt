package com.example.newsapp.api

import NewsAPIService
import com.example.newsapp.constants.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIInstance {
    private val retrofit by lazy{
        Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    }

    val api:NewsAPIService by lazy{
        retrofit.create(NewsAPIService::class.java)
    }
}