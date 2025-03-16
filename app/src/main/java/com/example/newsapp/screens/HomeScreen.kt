package com.example.newsapp.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Computer
import androidx.compose.ui.res.painterResource
import com.example.newsapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: ViewModelHomeScreen = ViewModelHomeScreen()) {
    val searchQuery = remember { mutableStateOf("") }
    val newsResponse by viewModel.newsResponse.observeAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val categories = listOf("General", "Business", "Health", "Entertainment", "Science", "Sports", "Technology")
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: "") } // Default to first category

    val categoryIcons = mapOf(
        "General" to Icons.Default.Public,
        "Business" to Icons.Default.Business,
        "Health" to Icons.Default.LocalHospital,
        "Entertainment" to Icons.Default.Movie,
        "Science" to Icons.Default.Science,
        "Sports" to Icons.Default.SportsSoccer,
        "Technology" to Icons.Default.Computer
    )



    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // **App Logo and Name**
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(color = MaterialTheme.colorScheme.primary)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(90.dp)
                                .align(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "HIGH News",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Get HIGH on information",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // **Categories Section**
                Text(
                    text = "Categories",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(8.dp)
                )

                categories.forEach { category ->
                    val isSelected = category == selectedCategory

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .clickable {
                                selectedCategory = category
                                coroutineScope.launch { drawerState.close() } // Close drawer on selection
                                viewModel.fetchTopHeadlines(category.lowercase()) // Trigger API call
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .background(
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(topEnd = 25.dp, bottomEnd = 25.dp)
                                )
                                .padding(start = 16.dp, top = 12.dp, bottom = 12.dp)
                        ) {
                            Text(
                                text = category,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(8.dp)
        ) {
            SearchBar(
                searchQuery = searchQuery,
                onMenuClick = { coroutineScope.launch { drawerState.open() } },
                navController = navController // Pass the actual NavController
            )
            NewsList(newsResponse?.articles, navController)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(searchQuery: MutableState<String>, onMenuClick: () -> Unit , navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { onMenuClick() }, // Open sidebar
            modifier = Modifier.padding(vertical = 5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu Icon",
                tint = MaterialTheme.colorScheme.onSurface
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
                    tint = MaterialTheme.colorScheme.onSurface
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors()
        )

        IconButton(
            onClick = { navController.navigate("profile_screen") }, // Navigate to Profile Screen
            modifier = Modifier.padding(vertical = 5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Icon",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


@Composable
fun NewsList(articles: List<Article>? , navController: NavController) {
    LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        articles?.filter { !it.urlToImage.isNullOrEmpty() }?.let { filteredArticles ->
            if (filteredArticles.isNotEmpty()) {
                items(filteredArticles) { article ->
                    ArticleCard(article , navController)
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
fun ArticleCard(article: Article, navController: NavController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    val json = Uri.encode(Gson().toJson(article)) // Convert Article to JSON string
                    navController.navigate("news_detail/$json")
                }
            ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            // **Full-Size Image Without Curves**
            AsyncImage(
                model = article.urlToImage,
                contentDescription = article.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp), // Set a fixed height without clipping
                contentScale = ContentScale.Crop // Crop to maintain aspect ratio
            )

            Spacer(modifier = Modifier.height(8.dp))

            // **Title & Source Section**
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = article.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = article.source?.name ?: "Unknown Source",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Text(
                        text = article.publishedAt?.toFormattedDate() ?: "",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}


fun String.toFormattedDate(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
        val date = inputFormat.parse(this)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        this
    }
}
