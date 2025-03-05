package com.example.newsapp

import com.example.newsapp.api.APIInstance
import com.example.newsapp.api.NewsResponse
import android.util.Log
import retrofit2.Response

class NewsRepository {
    companion object {
        private val apiService by lazy { APIInstance.api }

        suspend fun getTopHeadlines(): NewsResponse? {
            return try {
                Log.d("NewsRepository", "Fetching top headlines...")
                val response: Response<NewsResponse> = apiService.getTopHeadlines()

                if (response.isSuccessful) {
                    Log.d("NewsRepository", "API success: ${response.body()}")
                    response.body()
                } else {
                    Log.e("NewsRepository", "API failed: ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("NewsRepository", "Exception: ${e}")
                null
            }
        }
    }
}