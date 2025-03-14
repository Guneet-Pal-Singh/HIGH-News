package com.example.newsapp.api

data class Result(
    val ai_org: String,
    val ai_region: Any,
    val ai_tag: String,
    val article_id: String,
    val category: List<String>,
    val content: String,
    val country: List<String>,
    val creator: Any,
    val description: String,
    val duplicate: Boolean,
    val image_url: String,
    val keywords: Any,
    val language: String,
    val link: String,
    val pubDate: String,
    val pubDateTZ: String,
    val sentiment: String,
    val sentiment_stats: Any,
    val source_icon: String,
    val source_id: String,
    val source_name: String,
    val source_priority: Int,
    val source_url: String,
    val title: String,
    val video_url: Any
)