package com.example.newsapp.screens

import android.util.Log
import androidx.lifecycle.*
import com.example.newsapp.NewsRepository
import com.example.newsapp.api.NewsResponse
import kotlinx.coroutines.launch

class ViewModelHomeScreen(private val location: String) : ViewModel() {

    private val _newsResponse = MutableLiveData<NewsResponse?>()
    val newsResponse: LiveData<NewsResponse?> = _newsResponse

    private val _newsResponseByLocation = MutableLiveData<NewsResponse?>()
    val newsResponseByLocation: LiveData<NewsResponse?> = _newsResponseByLocation

    init {
        fetchTopHeadlines("general")
        fetchArticlesByLocation()
    }

    fun fetchTopHeadlines(category: String) {
        viewModelScope.launch {
            try {
                val response = NewsRepository.getTopHeadlines(category)
                _newsResponse.value = response
                Log.d("ViewModelHomeScreen", "Fetched: ${response?.status}")
            } catch (e: Exception) {
                _newsResponse.value = null
                Log.e("ViewModelHomeScreen", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun searchArticles(query: String) {
        viewModelScope.launch {
            try {
                val response = NewsRepository.searchArticles(query)
                _newsResponse.value = response
                Log.d("ViewModelHomeScreen", "Search fetched: ${response?.status}")
            } catch (e: Exception) {
                _newsResponse.value = null
                Log.e("ViewModelHomeScreen", "Search error: ${e.localizedMessage}")
            }
        }
    }

    fun fetchArticlesByLocation() {
        viewModelScope.launch {
            try {
                val response = NewsRepository.getNewsHeadlinesByLocation(location)
                _newsResponseByLocation.value = response
                Log.d("ViewModelHomeScreen", "Fetched by location: ${response?.status}")
                Log.d("ViewModelHomeScreen", "response: ${response?.articles}")
            } catch (e: Exception) {
                _newsResponseByLocation.value = null
                Log.e("ViewModelHomeScreen", "Error fetching by location: ${e.localizedMessage}")
            }
        }
    }
}