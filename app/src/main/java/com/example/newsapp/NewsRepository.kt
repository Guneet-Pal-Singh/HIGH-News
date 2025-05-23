package com.example.newsapp

import android.util.Log
import com.example.newsapp.api.APIInstance
import com.example.newsapp.api.NewsResponse
import com.example.newsapp.constants.Constants
import retrofit2.Response
import java.util.Locale

class NewsRepository {
    companion object {
        private val apiService by lazy { APIInstance.api }

        suspend fun getTopHeadlines(category: String = "general"): NewsResponse? {
            return try {
                Log.d("NewsRepository", "Fetching top headlines for category: $category")
                val response: Response<NewsResponse> = apiService.getTopHeadlines(
                    countryCode = "us",
                    category = category,
                    apiKey = Constants.API_KEY
                )

                if (response.isSuccessful) {
                    Log.d("NewsRepository", "API success: ${response.body()}")
                    response.body()
                } else {
                    Log.e("NewsRepository", "API failed: ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("NewsRepository", "Exception: ${e.localizedMessage}", e)
                null
            }
        }

        suspend fun getEverything(query: String, sortBy: String? = null): NewsResponse? {
            return try {
                Log.d("NewsRepository", "Fetching everything for query: $query")
                val response: Response<NewsResponse> = apiService.getEverything(
                    q = query,
                    pageSize = 10,
                    page = 1,
                    sortBy = sortBy,
                    apiKey = Constants.API_KEY
                )

                if (response.isSuccessful) {
                    Log.d("NewsRepository", "API success: ${response.body()}")
                    response.body()
                } else {
                    Log.e("NewsRepository", "API failed: ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("NewsRepository", "Exception: ${e::class.qualifiedName}", e)
                null
            }
        }

        // 🔧 NEW FUNCTION added to resolve searchArticles error
        suspend fun searchArticles(query: String): NewsResponse? {
            return getEverything(query)
        }

        suspend fun getNewsHeadlinesByLocation(countryCode: String,category: String="general"): NewsResponse?{
            return try{
                Log.d("NewsRepository", "Fetching top headlines for category: $category")
                val response: Response<NewsResponse> = apiService.getTopHeadlines(
                    countryCode = countryCode,
                    category = category,
                    apiKey = Constants.API_KEY
                )

                if (response.isSuccessful) {
                    Log.d("NewsRepository", "API success: ${response.body()}")
                    response.body()
                } else {
                    Log.e("NewsRepository", "API failed: ${response.errorBody()?.string()}")
                    null
                }
            }catch(e: Exception){
                Log.e("NewsRepository", "Exception: ${e::class.qualifiedName}", e)
                null
            }
        }
    }
}
