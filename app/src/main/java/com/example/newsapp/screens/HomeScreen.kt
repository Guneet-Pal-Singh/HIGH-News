package com.example.newsapp.screens

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.newsapp.api.Article

@Composable
fun HomeScreen(navController: NavController, viewModel: ViewModelHomeScreen=ViewModelHomeScreen()) {
    val newsResponse by viewModel.newsResponse.observeAsState()
    Column(modifier = Modifier.fillMaxSize()){
        LazyColumn(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.90f)) {items(newsResponse?.articles.orEmpty()){article->
                ArticleCard(article)
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
fun ArticleCard(article: Article){
    Card(modifier = Modifier.padding(8.dp)){
        Column(modifier = Modifier.padding(8.dp)){
            Row(modifier = Modifier.fillMaxWidth()){
                AsyncImage(
                    model = article.urlToImage,
                    contentDescription = article.title,
                    modifier = Modifier.size(100.dp)
                )
                Column(modifier = Modifier.weight(1.0f)) {
                    Text(article.title, fontWeight = FontWeight.Bold, style = TextStyle(fontSize = 20.sp))
                    Text(article.description)
                }
            }

            Row(modifier = Modifier){
                Text(text=article.source.name)
                Spacer(modifier = Modifier.padding(8.dp))
                Text(text=article.publishedAt)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun card(){
    ArticleCard(Article("author","content","description","publishedAt",com.example.newsapp.api.Source("id","name"),"title","url","https://drive.google.com/file/d/1TlQDeL8LfqV-SgQK-duaaAQyq1u0EtYJ/view?usp=sharing"))
}