package com.example.newsapp

import com.example.newsapp.api.APIInstance
import com.example.newsapp.api.NewsResponse
import android.util.Log
import retrofit2.Response

class NewsRepository {
    companion object {
        private val apiService by lazy { APIInstance.api }

        suspend fun getTopHeadlines(category: String = "general"): NewsResponse? {
            return try {
                Log.d("NewsRepository", "Fetching top headlines for category: $category")
                val response: Response<NewsResponse> = apiService.getTopHeadlines(
                    countryCode = "us",
                    category = category,
                )

                if (response.isSuccessful) {
                    Log.d("NewsRepository", "API success: ${response.body()}")
                    response.body()
                } else {
                    Log.e("NewsRepository", "API failed: ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("NewsRepository", "Exception: ${e.localizedMessage}")
                null
            }
        }

        suspend fun getEverything(query: String,sortBy: String? = null): NewsResponse? {
            return try {
                Log.d("NewsRepository", "Fetching everything for query: $query")
                val response: Response<NewsResponse> = apiService.getEverything(
                    q = query,
                    pageSize = 10,
                    page = 1
                )

                if (response.isSuccessful) {
                    Log.d("NewsRepository", "API success: ${response.body()}")
                    response.body()
                } else {
                    Log.e("NewsRepository", "API failed: ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("NewsRepository", "Exception: ${e::class.qualifiedName}",e)
                null
            }
        }
    }
}
