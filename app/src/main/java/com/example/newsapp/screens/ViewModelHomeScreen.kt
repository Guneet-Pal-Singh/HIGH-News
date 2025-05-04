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

    private val _newsResponseGlobal = MutableLiveData<NewsResponse?>()
    val newsResponseGlobal: LiveData<NewsResponse?> = _newsResponseGlobal

    init {
        fetchTopHeadlines("general")
        fetchArticlesByLocation()
        fetchGlobalArticles()
    }

    fun fetchTopHeadlines(category: String) {
        viewModelScope.launch {
            try {
                val response = NewsRepository.getTopHeadlines(category)
                _newsResponse.value = response
                Log.d("ViewModelHomeScreen", "Fetched category: ${response?.status}")
            } catch (e: Exception) {
                _newsResponse.value = null
                Log.e("ViewModelHomeScreen", "Error fetching category: ${e.localizedMessage}")
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
            } catch (e: Exception) {
                _newsResponseByLocation.value = null
                Log.e("ViewModelHomeScreen", "Error by location: ${e.localizedMessage}")
            }
        }
    }

    fun fetchGlobalArticles() {
        viewModelScope.launch {
            try {
                val response = NewsRepository.getNewsHeadlinesByLocation("us")
                _newsResponseGlobal.value = response
                Log.d("ViewModelHomeScreen", "Fetched global: ${response?.status}")
            } catch (e: Exception) {
                _newsResponseGlobal.value = null
                Log.e("ViewModelHomeScreen", "Global fetch error: ${e.localizedMessage}")
            }
        }
    }
    fun fetchTopHeadlinesGlobal(category: String) {
        viewModelScope.launch {
            try {
                val response = NewsRepository.getTopHeadlines(category)
                _newsResponseGlobal.value = response
                Log.d("ViewModelHomeScreen", "Fetched category: ${response?.status}")
            } catch (e: Exception) {
                _newsResponseGlobal.value = null
                Log.e("ViewModelHomeScreen", "Error fetching category: ${e.localizedMessage}")
            }
        }
    }
}
