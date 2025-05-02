package com.example.newsapp.api

import com.example.newsapp.api.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPIService {

    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") countryCode: String? = null,
        @Query("category") category: String? = null,
        @Query("sources") sources: String? = null,
        @Query("q") q: String? = null,
        @Query("pageSize") pageSize: Int? = null,
        @Query("page") page: Int? = null,
        @Query("apiKey") apiKey: String
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun getEverything(
        @Query("q") q: String,
        @Query("pageSize") pageSize: Int? = null,
        @Query("page") page: Int? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("language") language: String? = null,
        @Query("sources") sources: String? = null,
        @Query("domains") domains: String? = null,
        @Query("excludeDomains") excludeDomains: String? = null,
        @Query("apiKey") apiKey: String
    ): Response<NewsResponse>
}
