package com.example.newsapp.screens

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.newsapp.api.Article
import com.example.newsapp.db.BookmarkEntity
import kotlinx.coroutines.flow.collectLatest
import java.util.*
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NewsDetailScreen(navController: NavController, article: Article,viewModelProfileScreen: ViewModelProfileScreen) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModelProfileScreen.toastMessage.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

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

    val context = LocalContext.current
    // --- TTS State and Logic ---
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isSpeaking by remember { mutableStateOf(false) }
    var currentChunk by remember { mutableStateOf(0) }

    // Prepare paragraphs: title, description (split by newline), and content
    val descriptionParagraphs = (article.description ?: "")
        .split(Regex("\n+"))
        .filter { it.isNotBlank() }
    val contentParagraphs = (article.content ?: "Content unavailable")
        .replace(Regex("\\[\\+\\d+ chars]"), "")
        .split(Regex("\n+"))
        .filter { it.isNotBlank() }

    val allParagraphs = listOf(cleanedTitle) + descriptionParagraphs + contentParagraphs

    // Chunk paragraphs for TTS limit
    val ttsChunks = remember(allParagraphs) {
        val maxLen = 3500
        allParagraphs.flatMap { paragraph ->
            if (paragraph.length > maxLen) paragraph.chunked(maxLen)
            else listOf(paragraph)
        }
    }

    val customLines = listOf(
        "To bookmark the article, click on the 'Bookmark' button below on the left",
        "To read full article, click on the 'Read More' button below on the right"
    )

    val ttsSequence = remember(cleanedTitle, descriptionParagraphs, contentParagraphs) {
        val seq = mutableListOf<Pair<String?, Long?>>()
        seq.add(cleanedTitle to null) // Title
        seq.add(null to 1000L)        // 1-second pause (1000 ms)
        descriptionParagraphs.forEach { seq.add(it to null) }
        contentParagraphs.forEach { seq.add(it to null) }
        seq.add(null to 500L)        // 0.5-second pause (500 ms)
        customLines.forEach { seq.add(it to null) }
        seq
    }

    fun speakNextChunk(
        tts: TextToSpeech?,
        sequence: List<Pair<String?, Long?>>,
        index: Int
    ) {
        val (text, pause) = sequence[index]
        if (pause != null) {
            tts?.playSilentUtterance(pause, TextToSpeech.QUEUE_FLUSH, "PAUSE_$index")
        } else if (text != null) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "CHUNK_$index")
        }
    }

    DisposableEffect(ttsSequence) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
            }
        }
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) { isSpeaking = true }
            override fun onDone(utteranceId: String?) {
                if (currentChunk < ttsSequence.size - 1) {
                    currentChunk++
                    speakNextChunk(tts, ttsSequence, currentChunk)
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

            // --- Read Aloud Button ---
            Button(
                onClick = {
                    if (!isSpeaking) {
                        currentChunk = 0
                        tts?.speak(
                            ttsChunks[0],
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "CHUNK_0"
                        )
                    } else {
                        tts?.stop()
                        isSpeaking = false
                        currentChunk = 0
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = if (isSpeaking) Icons.Filled.VolumeOff else Icons.Filled.VolumeUp,
                    contentDescription = "Read Aloud",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (isSpeaking) "Stop" else "Read Aloud",
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
