package com.example.newsapp.screens

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.NewsRepository
import com.example.newsapp.api.NewsResponse
import kotlinx.coroutines.launch

class ViewModelHomeScreen : ViewModel() {
    private val _newsResponse = MutableLiveData<NewsResponse?>()
    val newsResponse: LiveData<NewsResponse?> = _newsResponse

    init {
        fetchTopHeadlines("general") // Default category
    }

    // Fetch articles based on the selected category
    fun fetchTopHeadlines(category: String) {
        viewModelScope.launch {
            Log.d("ViewModelHomeScreen", "Fetching $category news from repository...")

            try {
                val response: NewsResponse? = NewsRepository.getTopHeadlines(category)
                _newsResponse.value = response

                if (response != null) {
                    Log.d("ViewModelHomeScreen", "Response received: ${response.status}")
                } else {
                    Log.e("ViewModelHomeScreen", "Response is null")
                }
            } catch (e: Exception) {
                _newsResponse.value = null
                Log.e("ViewModelHomeScreen", "Error: ${e.localizedMessage}")
            }
        }
    }

    // Fetch articles based on the search query
    fun searchArticles(query: String) {
        viewModelScope.launch {
            Log.d("ViewModelHomeScreen", "Searching news with query: $query")

            try {
                val response: NewsResponse? = NewsRepository.searchArticles(query)
                _newsResponse.value = response

                if (response != null) {
                    Log.d("ViewModelHomeScreen", "Search response received: ${response.status}")
                } else {
                    Log.e("ViewModelHomeScreen", "Search response is null")
                }
            } catch (e: Exception) {
                _newsResponse.value = null
                Log.e("ViewModelHomeScreen", "Error: ${e.localizedMessage}")
            }
        }
    }
}
