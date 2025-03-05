package com.example.newsapp

import NewsAPIService
import com.example.newsapp.api.APIInstance

class NewsRepository {
    companion object {
        private val apiService = APIInstance.api

        suspend fun getTopHeadlines() = apiService.getTopHeadlines()
    }
}