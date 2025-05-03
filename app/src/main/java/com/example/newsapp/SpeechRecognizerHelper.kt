package com.example.newsapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleOwner

class SpeechRecognizerHelper(
    private val context: Context,
    private val onResult: (String) -> Unit
) {
    private var speechRecognizer: SpeechRecognizer? = null

    fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Toast.makeText(context, "Speech recognition not available", Toast.LENGTH_SHORT).show()
            return
        }

        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Log.d("SpeechRecognition", "Ready for speech")
                }

                override fun onBeginningOfSpeech() {
                    Log.d("SpeechRecognition", "Speech started")
                }

                override fun onRmsChanged(rmsdB: Float) {
                    // Called when the RMS (root mean square) value of the audio changes.
                    // You can use this for visualizing the audio input level, if needed.
                }

                override fun onBufferReceived(buffer: ByteArray?) {
                    // Called when a portion of the audio buffer is received.
                    // This can be useful for streaming recognition or providing visual feedback.
                }

                override fun onEndOfSpeech() {
                    // Called when the user stops speaking.
                    Log.d("SpeechRecognition", "Speech ended")
                }

                override fun onError(error: Int) {
                    val errorMessage = when (error) {
                         SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                         SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                         SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                         SpeechRecognizer.ERROR_NETWORK -> "Network error"
                         SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                         SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
                         SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                         SpeechRecognizer.ERROR_SERVER -> "Server error"
                         SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                        else -> "Unknown error"
                    }
                    Log.e("SpeechRecognition", errorMessage)
//                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val bestMatch = matches?.firstOrNull() ?: ""
                    Log.d("SpeechRecognition", "Results: $bestMatch")
                    onResult(bestMatch)
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    // Called when partial recognition results are available.
                    // This is useful for providing real-time feedback as the user speaks.
                }

                override fun onEvent(eventType: Int, params: Bundle?) {
                    // Reserved for future use.
                }
            })
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        try {
            speechRecognizer?.startListening(intent)
        } catch (e: SecurityException) {
            Log.e("SpeechRecognition", "Security exception: ${e.message}")
            Toast.makeText(context, "Microphone permission required", Toast.LENGTH_SHORT).show()
        }
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}