package com.example.newsapp.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.newsapp.api.NewsResponse
import com.example.newsapp.api.Result
import coil.compose.AsyncImage
import com.example.newsapp.R
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
            .background(color = MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {
        SearchBar(searchQuery)
        NewsList(newsResponse?.results , navController)
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
            onClick = { /* TODO: Open profile */ },
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
fun NewsList(results: List<Result>?, navController: NavController) {
    LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        results?.filter { !it.image_url.isNullOrEmpty() }?.let { filteredresults ->
            if (filteredresults.isNotEmpty()) {
                items(filteredresults) { result ->
                    ResultCard(result , navController)
                }
            } else {
                item {
                    Text(
                        "No results available",
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
fun ResultCard(result: Result, navController: NavController) {
    Card(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    // Handle click
                }
            ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(10.dp)
        ) {
            // News Image
            if (result.image_url != null){
                AsyncImage(
                    model = result.image_url,
                    contentDescription = result.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.TopCenter
                )
            }else{
                Image(
                    painter = painterResource(id = R.drawable.no_image),
                    contentDescription = "News Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = result.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(5.dp))

            // Row for Date (Left) and Source (Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text =  result.pubDate,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Text(
                    text = result.source_name,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.End
                )
            }
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ResultCard(result = Result("abc","abc"
        ,"abc","abc",
        listOf("abc"),"abc",listOf("abc"),
        "abc","abc",false,
        "abc","abc","abc",
        "abc","abc","abc",
        "abc","abc","abc",
        "abc","abc",0,
        "abc","abc","abc",),navController = NavController(LocalContext.current))
}