package com.example.newsapp.screens

import TranslationViewModel
import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.newsapp.R
import com.example.newsapp.api.Article
import com.example.newsapp.db.BookmarkEntity
import kotlinx.coroutines.flow.collectLatest
import java.util.*


@Composable
fun ArticleDetailScreen(
    navController: NavController,
    title: String,
    imageUrl: String,
    url: String,
    description: String
) {
    val context = LocalContext.current
    val translateViewModel = viewModel<TranslationViewModel>()
    var translate by remember { mutableStateOf(false) }
    val translatedTexts by translateViewModel.translatedText.collectAsState()
    var showWebView by remember { mutableStateOf(false) }

    val defaultTexts = listOf(title, description)
    val currentTexts = if (translate) {
        if (translatedTexts.size >= 2) translatedTexts else defaultTexts
    } else {
        defaultTexts
    }

    val ttsChunks = remember(currentTexts) {
        val maxLen = 3500
        currentTexts.flatMap { text ->
            if (text.length > maxLen) text.chunked(maxLen) else listOf(text)
        }
    }

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isSpeaking by remember { mutableStateOf(false) }
    var currentChunk by remember { mutableStateOf(0) }

    fun speakNextChunk(tts: TextToSpeech?, chunks: List<String>, index: Int) {
        if (index < chunks.size) {
            tts?.speak(chunks[index], TextToSpeech.QUEUE_FLUSH, null, "CHUNK_$index")
        }
    }

    DisposableEffect(ttsChunks) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = if (translate) Locale("hi", "IN") else Locale("en", "IN")
            }
        }
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) { isSpeaking = true }
            override fun onDone(utteranceId: String?) {
                if (currentChunk < ttsChunks.size - 1) {
                    currentChunk++
                    speakNextChunk(tts, ttsChunks, currentChunk)
                } else {
                    isSpeaking = false
                    currentChunk = 0
                }
            }
            override fun onError(utteranceId: String?) { isSpeaking = false }
        })
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    if (showWebView) {
        WebViewScreen(url = url) // Pass the article URL if you have it, else leave blank
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .padding(bottom = 100.dp)
            ) {
                Text(
                    text = currentTexts[0],
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
                    if (imageUrl == "empty") {
                        Image(
                            painter = painterResource(id = R.drawable.placeholder_image),
                            contentDescription = "Article Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Article Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Text(
                    text = currentTexts[1],
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Serif),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            // Fixed Bottom Buttons
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Button(
                        onClick = {
                            if (isSpeaking) {
                                tts?.stop()
                                isSpeaking = false
                                currentChunk = 0
                            } else {
                                tts?.language = if (translate) Locale("hi", "IN") else Locale("en", "IN")
                                currentChunk = 0
                                speakNextChunk(tts, ttsChunks, currentChunk)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(0.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Filled.VolumeOff else Icons.Filled.VolumeUp,
                            contentDescription = "Read Aloud",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isSpeaking) "Stop" else "Read Aloud")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            translate = !translate
                            translateViewModel.translateTitle(
                                translate = translate,
                                sourceText = defaultTexts
                            )
                            tts?.language = if (translate) Locale("hi", "IN") else Locale("en", "IN")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Translate")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showWebView = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Read More")
                }
            }
        }
    }
}
