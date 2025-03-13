package com.example.newsapp.screens

import android.graphics.Color.BLUE
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.newsapp.api.Article
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import java.util.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
import java.util.*


@Composable
fun HomeScreen(navController: NavController, viewModel: ViewModelHomeScreen=ViewModelHomeScreen()) {
    val newsResponse by viewModel.newsResponse.observeAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.90f)) {
            newsResponse?.articles?.let { articles ->
                items(articles) { article ->
                    ArticleCard(article)
                }
            } ?: run {
                item {
                    Text("No articles available")
                }
            }
        }


        Row(modifier = Modifier.fillMaxSize()) {
            Button(onClick = {}, modifier = Modifier.padding(3.dp)) {
                Text("Search News")
            }

            Button(onClick = {}, modifier = Modifier.padding(3.dp)) {
                Text("Saved News")
            }

            Button(onClick = {navController.popBackStack()}, modifier = Modifier.padding(3.dp)) {
                Text("Back")
            }
        }
    }
}





@Composable
fun ArticleCard(article: Article) {
    Card(
        modifier = Modifier.padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp) // Rounded card
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = article.urlToImage,
                contentDescription = article.title,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(30.dp)) // Rounded image
            )

            Spacer(modifier = Modifier.width(12.dp)) // Space between image & text

            Column(modifier = Modifier.padding(start = 8.dp)) {
                // Title only (no publisher name here)
                Text(
                    text = article.title,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(fontSize = 16.sp)
                )

                Spacer(modifier = Modifier.height(4.dp)) // Space between title and publisher/date

                // Publisher & Date (appears only here)
                Text(
                    text = " ${article.publishedAt.toFormattedDate()} ",
                    style = TextStyle(fontSize = 12.sp, color = Color.Gray)
                )
            }
        }
    }
}

// Function to format date
fun String.toFormattedDate(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
        val date = inputFormat.parse(this)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        this // Return original if parsing fails
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCard() {
    ArticleCard(
        Article(
            "author", "content", "description", "2025-03-12T04:43:41Z",
            com.example.newsapp.api.Source("id", "BBC News"),
            "UK helped Ukraine and US reach ceasefire agreement",
            "url",
            "https://drive.google.com/file/d/1TlQDeL8LfqV-SgQK-duaaAQyq1u0EtYJ/view?usp=sharing"
        )
    )
}




//@Composable
//fun ArticleCard(article: Article) {
//    Log.d("ArticleCard", "Title: ${article.title}, Description: ${article.description}")
//    Card(modifier = Modifier.padding(8.dp)) {
//        Column(modifier = Modifier.padding(8.dp)) {
//            Row(modifier = Modifier.fillMaxWidth()) {
//                AsyncImage(
//                    model = article.urlToImage,
//                    contentDescription = article.title,
//                    modifier = Modifier.size(100.dp)
//                )
//                Column(modifier = Modifier.weight(1.0f)) {
//                    Text(article.title, fontWeight = FontWeight.Bold, style = TextStyle(fontSize = 20.sp))
//                    Text(article.description ?: "No description available")
//                }
//            }
//
//            Row(modifier = Modifier) {
//                Text(text = article.source.name)
//                Spacer(modifier = Modifier.padding(8.dp))
//                Text(text = article.publishedAt)
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun card(){
//    ArticleCard(Article("author","content","description","publishedAt",com.example.newsapp.api.Source("id","name"),"title","url","https://drive.google.com/file/d/1TlQDeL8LfqV-SgQK-duaaAQyq1u0EtYJ/view?usp=sharing"))
//}

//@Preview(showBackground = true)
//@Composable
//fun card(){
//    ArticleCard(Article("author","content","description","publishedAt",com.example.newsapp.api.Source("id","name"),"title","url","https://drive.google.com/file/d/1TlQDeL8LfqV-SgQK-duaaAQyq1u0EtYJ/view?usp=sharing"))
//}
