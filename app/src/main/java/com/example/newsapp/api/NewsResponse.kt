package com.example.newsapp.api

data class NewsResponse(
    val results: List<Result>?,
    val status: String,
    val totalResults: Int
)