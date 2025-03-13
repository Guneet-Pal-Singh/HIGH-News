package com.example.newsapp.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.newsapp.api.Article
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: ViewModelHomeScreen = ViewModelHomeScreen()) {
    val searchQuery = remember { mutableStateOf("") }
    val newsResponse by viewModel.newsResponse.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFECEFF1))
            .padding(8.dp)
    ) {
        SearchBar(searchQuery)
        NewsList(newsResponse?.articles)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(searchQuery: MutableState<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { /* TODO: Open menu */ },
            modifier = Modifier.padding(vertical = 5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu Icon",
                tint = Color.Black
            )
        }

        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 5.dp),
            placeholder = { Text("Search news...") },
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = Color.Black
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors()
        )

        IconButton(
            onClick = { /* TODO: Open profile */ },
            modifier = Modifier.padding(vertical = 5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Icon",
                tint = Color.Black
            )
        }
    }
}

@Composable
fun NewsList(articles: List<Article>?) {
    LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        articles?.filter { !it.urlToImage.isNullOrEmpty() }?.let { filteredArticles ->
            if (filteredArticles.isNotEmpty()) {
                items(filteredArticles) { article ->
                    ArticleCard(article)
                }
            } else {
                item {
                    Text(
                        "No articles available",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArticleCard(article: Article) {
    Card(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .combinedClickable (
                onClick = {
                    Log.e("Single Clicked","Single Clicked")
                },
                onDoubleClick= {
                    Log.e("Double Clicked","Double Clicked")
                }
            ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFFFFE0B2))
                .padding(10.dp)
        ) {
            AsyncImage(
                model = article.urlToImage,
                contentDescription = article.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = article.title,
                fontWeight = FontWeight.Bold,
                style = TextStyle(fontSize = 14.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = article.publishedAt.toFormattedDate(),
                fontWeight = FontWeight.Bold,
                style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

fun String.toFormattedDate(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("dd - MMMM - yyyy", Locale.ENGLISH)
        val date = inputFormat.parse(this)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        this
    }
}
