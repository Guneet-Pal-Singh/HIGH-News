package com.example.newsapp.api

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)