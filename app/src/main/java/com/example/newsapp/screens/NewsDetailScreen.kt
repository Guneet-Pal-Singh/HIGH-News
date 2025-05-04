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
import coil.compose.rememberAsyncImagePainter
import com.example.newsapp.api.Article
import com.example.newsapp.db.BookmarkEntity
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NewsDetailScreen(
    navController: NavController,
    article: Article,
    viewModelProfileScreen: ViewModelProfileScreen
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val translateViewModel = viewModel<TranslationViewModel>()

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
        NewsContent(
            article = article,
            onReadMoreClick = { showWebView = true },
            onBookmarkClick = { addBookmark() },
            translateViewModel = translateViewModel
        )
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
fun NewsContent(
    article: Article,
    onReadMoreClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    translateViewModel: TranslationViewModel
) {
    val cleanedTitle = article.title.removeSuffix(" - ${article.source.name}")
    val context = LocalContext.current
    var translate by remember { mutableStateOf(false) }
    val translatedTexts by translateViewModel.translatedText.collectAsState()

    val defaultTexts = listOf(cleanedTitle, article.description ?: "", article.content ?: "Content unavailable")

    val currentTexts = if (translate) {
        if (translatedTexts.size >= 3) translatedTexts else defaultTexts
    } else {
        defaultTexts
    }

    val title = currentTexts[0]
    val description = currentTexts[1]
    val content = currentTexts[2]


    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isSpeaking by remember { mutableStateOf(false) }
    var currentChunk by remember { mutableStateOf(0) }

    val descriptionParagraphs = (description)
        .split(Regex("\n+")).filter { it.isNotBlank() }

    val contentParagraphs = (content?: "Content unavailable")
        .replace(Regex("\\[\\+\\d+ chars]"), "")
        .split(Regex("\n+")).filter { it.isNotBlank() }

    val allParagraphs = currentTexts
    val ttsChunks = remember(allParagraphs) {
        val maxLen = 3500
        allParagraphs.flatMap { paragraph ->
            if (paragraph.length > maxLen) paragraph.chunked(maxLen) else listOf(paragraph)
        }
    }

    val customLines = listOf(
        "To bookmark the article, click on the 'Bookmark' button on the bottom left  of the screen",
        "To read full article, click on the 'Read More' button on the bottom right of the screen"
    )

    val ttsSequence = remember(title, description, content) {
        val seq = mutableListOf<Pair<String?, Long?>>()
        seq.add(title to null)
        seq.add(null to 1000L)
        descriptionParagraphs.forEach { seq.add(it to null) }
        contentParagraphs.forEach { seq.add(it to null) }
        seq.add(null to 500L)
        customLines.forEach { seq.add(it to null) }
        seq
    }

    fun speakNextChunk(tts: TextToSpeech?, sequence: List<Pair<String?, Long?>>, index: Int) {
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

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 100.dp) // Leave space for fixed buttons
        ) {
            Text(
                text = title,
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif),
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Source - ${article.source.name} | ${formatIndianTime(article.publishedAt)}",
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.SansSerif)
            )

            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(vertical = 20.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(article.urlToImage),
                    contentDescription = "News Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Text(
                text = description,
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Serif),
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Text(
                text = content.replace(Regex("\\[\\+\\d+ chars]"), "") + "\n\nTo read the full article, click on the 'Read More' button below.",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Serif),
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(bottom = 16.dp)
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
            Row{
                Button(
                    onClick = {
                        if (isSpeaking) {
                            tts?.stop()
                            isSpeaking = false
                            currentChunk = 0
                        } else {
                            currentChunk = 0
                            speakNextChunk(tts, ttsSequence, currentChunk)
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
                        translate=!translate

                        translateViewModel.translateTitle(
                            translate = translate,
                            sourceText = defaultTexts
                        )

                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Translate")
                }
            }

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
}
