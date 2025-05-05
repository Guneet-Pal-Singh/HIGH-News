package com.example.newsapp.screens

import TranslationViewModel
import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.newsapp.R
import com.example.newsapp.api.Article
import com.example.newsapp.db.BookmarkEntity
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@Composable
fun BookmarkDetailScreen(
    navController: NavController,
    bookmark: BookmarkEntity,
    viewModelProfileScreen: ViewModelProfileScreen
) {
    var showWebView by remember { mutableStateOf(false) }

    fun addBookmark() {
        viewModelProfileScreen.insertIfNotExists(bookmark)
    }

    if (showWebView) {
        WebViewScreen(bookmark.url)
    } else {
        BookmarkContent(
            bookmark = bookmark,
            onReadMoreClick = { showWebView = true },
            onBookmarkClick = { addBookmark() }
        )
    }
}

@Composable
fun BookmarkContent(
    bookmark: BookmarkEntity,
    onReadMoreClick: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 100.dp)
    ) {
        Text(
            text = bookmark.title,
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif),
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(vertical = 20.dp)
        ) {
            if (bookmark.imageURL == "empty") {
                Image(
                    painter = painterResource(id = R.drawable.placeholder_image),
                    contentDescription = "Bookmark Image",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                AsyncImage(
                    model = bookmark.imageURL,
                    contentDescription = "Bookmark Image",
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(id = R.drawable.placeholder_image),
                    error = painterResource(id = R.drawable.error_image)
                )
            }
        }

        Text(
            text = bookmark.description ?: "",
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Serif),
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Row(modifier = Modifier.fillMaxWidth()) {
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
                Text("Bookmark")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onReadMoreClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Read More")
            }
        }
    }
}

