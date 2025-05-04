import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TranslationViewModel : ViewModel() {
    private var translator: Translator? = null

    private var _translatedText =MutableStateFlow(listOf<String>())
    var translatedText: StateFlow<List<String>> = _translatedText

    fun translateTitle(
        translate:Boolean,
        sourceText: List<String>,
        sourceLang: String = TranslateLanguage.ENGLISH,
        targetLang: String = TranslateLanguage.HINDI
    ) {
        if (sourceText.isEmpty()) {
            _translatedText.value = emptyList()
            return
        }

        if (translate){
            _translatedText.value = List(sourceText.size) { "" } // Initialize with empty strings
        } else {
            _translatedText.value = sourceText // If not translating, just return the original text
            return
        }

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLang)
            .setTargetLanguage(targetLang)
            .build()

        translator = Translation.getClient(options)

        val conditions = DownloadConditions.Builder().requireWifi().build()

        sourceText.forEachIndexed { index,item->
            translator!!.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    translator!!.translate(item)
                        .addOnSuccessListener { result ->
                            _translatedText.value = _translatedText.value.toMutableList().apply {
                                while (this.size <= index) add("") // Ensure the list has enough capacity
                                this[index] = result
                            }
                        }
                        .addOnFailureListener {
                            _translatedText.value = _translatedText.value.toMutableList().apply { this[index] = "Translation failed" }
                        }
                }
                .addOnFailureListener {
                    _translatedText.value = _translatedText.value.toMutableList().apply { this[index] = "Download Failed" }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        translator?.close()
    }
}