package com.example.newsapp.screens

import ConnectivityObserver
import TranslationViewModel
import android.Manifest
import android.app.Activity
import android.net.Uri
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.newsapp.R
import com.example.newsapp.SpeechRecognizerHelper
import com.example.newsapp.api.Article
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: ViewModelHomeScreen) {
    val searchQuery = remember { mutableStateOf("") }
    val newsResponse by viewModel.newsResponseByLocation.observeAsState()
    val newsResponseGlobal by viewModel.newsResponseGlobal.observeAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val categories = listOf("General", "Business", "Health", "Entertainment", "Science", "Sports", "Technology")
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var isUsingGlobalNews by rememberSaveable { mutableStateOf(false) }

    var translateText by remember { mutableStateOf(false) }
    val translateViewModel = viewModel<TranslationViewModel>()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val connectivityObserver = remember { ConnectivityObserver(context) }
    val isConnected by connectivityObserver.isConnected.collectAsState()

    DisposableEffect(lifecycleOwner) {
        onDispose {
            connectivityObserver.unregister(context)
        }
    }

    LaunchedEffect(isConnected) {
        if (isConnected) {
            if (isUsingGlobalNews) {
                viewModel.fetchTopHeadlinesGlobal(selectedCategory.lowercase())
            } else {
                viewModel.fetchTopHeadlines(selectedCategory.lowercase())
            }
        }
    }

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
                            .background(MaterialTheme.colorScheme.primary)
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
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Get HIGH on information",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "CATEGORIES",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    letterSpacing = 1.2.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                categories.forEach { category ->
                    val isSelected = category == selectedCategory
                    val icon = categoryIcons[category] ?: Icons.Default.Public

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                RoundedCornerShape(topEnd = 25.dp, bottomEnd = 25.dp)
                            )
                            .clickable {
                                selectedCategory = category
                                coroutineScope.launch { drawerState.close() }
                                searchQuery.value = ""
                                if (isUsingGlobalNews) {
                                    viewModel.fetchTopHeadlinesGlobal(category.lowercase())
                                } else {
                                    viewModel.fetchTopHeadlines(category.lowercase())
                                }
                            }
                            .padding(vertical = 12.dp, horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "$category Icon",
                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = category,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { translateText = !translateText },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Translate",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            SearchBar(
                searchQuery = searchQuery,
                onMenuClick = { coroutineScope.launch { drawerState.open() } },
                navController = navController,
                isUsingGlobalNews = isUsingGlobalNews,
                onSearch = { query ->
                    if (isUsingGlobalNews) {
                        viewModel.searchArticlesGlobal(query)
                    } else {
                        viewModel.searchArticles(query)
                    }
                }
            )
            if (!isConnected) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No internet connection", color = MaterialTheme.colorScheme.error, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.navigate("profile_screen") }) {
                        Text("Go to Profile")
                    }
                }
            }else{
                NewsList(
                    articles = if (isUsingGlobalNews) newsResponseGlobal?.articles else newsResponse?.articles,
                    navController = navController,
                    onShowGlobalNews = {
                        isUsingGlobalNews = true
                        viewModel.fetchTopHeadlinesGlobal(selectedCategory.lowercase())
                    },
                    translationViewModel = translateViewModel,
                    translateText = translateText
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SearchBar(
    searchQuery: MutableState<String>,
    onMenuClick: () -> Unit,
    navController: NavController,
    isUsingGlobalNews: Boolean,
    onSearch: (String) -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity
    val speechRecognizerHelper = remember {
        SpeechRecognizerHelper(activity) { result ->
            val trimmedResult = result.trim()
            if (trimmedResult.isNotEmpty()) {
                searchQuery.value = trimmedResult
                onSearch(trimmedResult)
            }
        }
    }
    val permissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizerHelper.destroy()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMenuClick) {
            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onSurface)
        }

        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = {
                searchQuery.value = it
                if (it.isNotBlank()) {
                    onSearch(it)
                }
            },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            placeholder = { Text("Search news...") },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurface)
            },
            trailingIcon = {
                IconButton(onClick = {
                    when {
                        permissionState.status.isGranted -> {
                            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                                speechRecognizerHelper.startListening()
                            } else {
                                Toast.makeText(context, "Speech recognition not available", Toast.LENGTH_SHORT).show()
                            }
                        }
                        permissionState.status.shouldShowRationale -> {
                            Toast.makeText(context, "Microphone permission required for voice search", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            permissionState.launchPermissionRequest()
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors()
        )

        IconButton(onClick = { navController.navigate("profile_screen") }) {
            Icon(Icons.Default.Person, contentDescription = "Profile", tint = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun NewsList(
    articles: List<Article>?,
    navController: NavController,
    onShowGlobalNews: (() -> Unit)? = null,
    translationViewModel: TranslationViewModel,
    translateText: Boolean
) {
    val titles = articles?.map { it.title } ?: emptyList()

    LaunchedEffect(translateText, titles) {
        if (translateText) {
            translationViewModel.translateTitle(true, titles)
        }
    }

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        articles?.filter { !it.urlToImage.isNullOrEmpty() }?.let { filteredArticles ->
            if (filteredArticles.isNotEmpty()) {
                items(filteredArticles) { article ->
                    val index = filteredArticles.indexOf(article)
                    val translatedTitle = if (translateText)
                        translationViewModel.translatedText.value.getOrNull(index) ?: article.title
                    else article.title

                    ArticleCard(article, navController, translationViewModel, translatedTitle)
                }
            } else {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No articles available", textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onShowGlobalNews?.invoke() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Show Global News")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArticleCard(article: Article, navController: NavController, translationViewModel: TranslationViewModel, title: String) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    val json = Uri.encode(Gson().toJson(article))
                    navController.navigate("news_detail/$json")
                }
            ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.inverseOnSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
            AsyncImage(
                model = article.urlToImage,
                contentDescription = article.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
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