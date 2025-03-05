package com.example.newsapp.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController, viewModel: ViewModelHomeScreen=ViewModelHomeScreen()) {
    val newsResponse by viewModel.newsResponse.observeAsState()

    LazyColumn {
        newsResponse?.articles?.let { articles ->
            items(articles) { article ->
                Text(text = article.title)
            }
        }
    }
}