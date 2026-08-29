package com.example.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.data.model.Poem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val currentPoemId: Int? = null,
    val currentVerseIndex: Int = 0,
    val speechRate: Float = 1.0f,
    val isTtsReady: Boolean = false,
    val errorMessage: String? = null
)

class PoemAudioPlayer(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var currentPoem: Poem? = null
    private var activeVerseIndex: Int = 0
    private var isSequentialPlay: Boolean = false

    private val _state = MutableStateFlow(AudioPlayerState())
    val state: StateFlow<AudioPlayerState> = _state.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val arLocale = Locale("ar")
            val langResult = tts?.setLanguage(arLocale)
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to default locale
                tts?.setLanguage(Locale.getDefault())
            }
            tts?.setSpeechRate(_state.value.speechRate)
            setupUtteranceListener()
            _state.update { it.copy(isTtsReady = true) }
        } else {
            _state.update { it.copy(isTtsReady = false, errorMessage = "تعذر تشغيل قارئ الصوت") }
        }
    }

    private fun setupUtteranceListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _state.update {
                    it.copy(isPlaying = true, isPaused = false, currentVerseIndex = activeVerseIndex)
                }
            }

            override fun onDone(utteranceId: String?) {
                if (isSequentialPlay && currentPoem != null) {
                    val poem = currentPoem!!
                    if (activeVerseIndex + 1 < poem.verses.size) {
                        activeVerseIndex++
                        playVerseInternal(poem.verses[activeVerseIndex].fullText, activeVerseIndex)
                    } else {
                        // Finished poem
                        stop()
                    }
                } else {
                    _state.update { it.copy(isPlaying = false, isPaused = false) }
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _state.update { it.copy(isPlaying = false, isPaused = false) }
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                _state.update { it.copy(isPlaying = false, isPaused = false) }
            }
        })
    }

    fun playPoem(poem: Poem, startVerseIndex: Int = 0) {
        if (tts == null) return
        currentPoem = poem
        activeVerseIndex = startVerseIndex.coerceIn(0, (poem.verses.size - 1).coerceAtLeast(0))
        isSequentialPlay = true

        _state.update {
            it.copy(
                currentPoemId = poem.id,
                currentVerseIndex = activeVerseIndex,
                isPlaying = true,
                isPaused = false
            )
        }

        if (poem.verses.isNotEmpty()) {
            playVerseInternal(poem.verses[activeVerseIndex].fullText, activeVerseIndex)
        } else {
            playVerseInternal(poem.title, 0)
        }
    }

    fun playSingleVerse(poem: Poem, verseIndex: Int) {
        if (tts == null) return
        currentPoem = poem
        activeVerseIndex = verseIndex
        isSequentialPlay = false

        _state.update {
            it.copy(
                currentPoemId = poem.id,
                currentVerseIndex = verseIndex,
                isPlaying = true,
                isPaused = false
            )
        }

        val verseText = poem.verses.getOrNull(verseIndex)?.fullText ?: poem.title
        playVerseInternal(verseText, verseIndex)
    }

    private fun playVerseInternal(text: String, index: Int) {
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "verse_$index")
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "verse_$index")
    }

    fun pause() {
        tts?.stop()
        _state.update { it.copy(isPlaying = false, isPaused = true) }
    }

    fun resume() {
        val poem = currentPoem
        if (poem != null) {
            playPoem(poem, activeVerseIndex)
        }
    }

    fun stop() {
        tts?.stop()
        isSequentialPlay = false
        _state.update {
            it.copy(isPlaying = false, isPaused = false, currentPoemId = null)
        }
    }

    fun nextVerse() {
        val poem = currentPoem ?: return
        if (activeVerseIndex + 1 < poem.verses.size) {
            activeVerseIndex++
            playVerseInternal(poem.verses[activeVerseIndex].fullText, activeVerseIndex)
        } else {
            stop()
        }
    }

    fun previousVerse() {
        val poem = currentPoem ?: return
        if (activeVerseIndex - 1 >= 0) {
            activeVerseIndex--
            playVerseInternal(poem.verses[activeVerseIndex].fullText, activeVerseIndex)
        }
    }

    fun setSpeechRate(rate: Float) {
        val safeRate = rate.coerceIn(0.5f, 2.0f)
        tts?.setSpeechRate(safeRate)
        _state.update { it.copy(speechRate = safeRate) }
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
