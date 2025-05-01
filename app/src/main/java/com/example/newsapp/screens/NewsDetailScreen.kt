package com.example.newsapp.screens

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.newsapp.api.Article
import com.example.newsapp.db.BookmarkEntity
import java.util.*

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NewsDetailScreen(navController: NavController, article: Article,viewModelProfileScreen: ViewModelProfileScreen) {
    var showWebView by remember { mutableStateOf(false) }

    fun addBookmark() {
        viewModelProfileScreen.insertIfNotExists(
            BookmarkEntity(
                id = 0,
                title = article.title,
                url = article.url,
                description = article.description,
                imageURL = article.urlToImage ?: "empty"
            )
        )
    }

    if (showWebView) {
        WebViewScreen(article.url)
    } else {
        NewsContent(article, onReadMoreClick = { showWebView = true }, onBookmarkClick = { addBookmark() })
    }
}

@Composable
fun WebViewScreen(url: String) {
    AndroidView(
        factory = { context ->
            android.webkit.WebView(context).apply {
                settings.javaScriptEnabled = true
                loadUrl(url)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

fun formatIndianTime(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        val date = inputFormat.parse(dateString)
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("en", "IN"))
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}

@Composable
fun NewsContent(article: Article, onReadMoreClick: () -> Unit, onBookmarkClick: () -> Unit) {
    val cleanedTitle = article.title.removeSuffix(" - ${article.source.name}")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = cleanedTitle,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            ),
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Source - ${article.source.name} | ${formatIndianTime(article.publishedAt)}",
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif
            ),
            textAlign = TextAlign.Start,
        )

        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(bottom = 25.dp, top = 25.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(article.urlToImage),
                contentDescription = "News Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            text = article.description,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Serif
            ),
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Text(
            text = (article.content ?: "Content unavailable")
                .replace(Regex("\\[\\+\\d+ chars]"), "") +
                    "\n\nTo read the full article, click on the 'Read More' button below.",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Serif
            ),
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onBookmarkClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Filled.Bookmark,
                    contentDescription = "Bookmark",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Bookmark",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                )
            }

            Spacer(Modifier.width(8.dp))

            Button(
                onClick = onReadMoreClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Read More", style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold))
            }
        }
    }
}
