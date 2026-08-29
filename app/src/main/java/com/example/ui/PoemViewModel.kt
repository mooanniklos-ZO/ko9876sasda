package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlayerState
import com.example.audio.PoemAudioPlayer
import com.example.data.PoemData
import com.example.data.local.AppDatabase
import com.example.data.local.UserPoemEntity
import com.example.data.model.Poem
import com.example.data.model.PoemCategory
import com.example.data.model.ReadingSettings
import com.example.data.repository.PoemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface Screen {
    data object Library : Screen
    data object Search : Screen
    data object Favorites : Screen
    data object BookInfo : Screen
    data class PoemDetail(val poemId: Int) : Screen
}

class PoemViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = PoemRepository(db.userPoemDao())
    val audioPlayer = PoemAudioPlayer(application)

    // Current Screen
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Library)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Search & Filtering
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(PoemCategory.ALL)
    val selectedCategory: StateFlow<PoemCategory> = _selectedCategory.asStateFlow()

    // Reading Settings
    private val _readingSettings = MutableStateFlow(ReadingSettings())
    val readingSettings: StateFlow<ReadingSettings> = _readingSettings.asStateFlow()

    // User metadata (favorites, bookmarks, notes)
    val userMetaMap: StateFlow<Map<Int, UserPoemEntity>> = repository.userMetaMap
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val latestBookmark: StateFlow<UserPoemEntity?> = repository.latestBookmark
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Audio Player state
    val audioState: StateFlow<AudioPlayerState> = audioPlayer.state

    // Daily Verse / Quote
    val dailyVerse: Pair<Poem, String> = PoemData.getVerseOfTheDay()

    // Filtered Poems
    val filteredPoems: StateFlow<List<Poem>> = combine(
        _searchQuery,
        _selectedCategory
    ) { query, category ->
        val list = if (query.isNotBlank()) {
            PoemData.searchPoems(query)
        } else {
            PoemData.getPoemsByCategory(category)
        }
        if (query.isNotBlank() && category != PoemCategory.ALL) {
            list.filter { it.category == category }
        } else {
            list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PoemData.allPoems)

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
        if (screen is Screen.PoemDetail) {
            recordRead(screen.poemId)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: PoemCategory) {
        _selectedCategory.value = category
    }

    fun updateReadingSettings(transform: (ReadingSettings) -> ReadingSettings) {
        _readingSettings.update(transform)
    }

    fun toggleFavorite(poemId: Int) {
        viewModelScope.launch {
            val isFav = userMetaMap.value[poemId]?.isFavorite ?: false
            repository.toggleFavorite(poemId, isFav)
        }
    }

    fun toggleBookmark(poemId: Int) {
        viewModelScope.launch {
            val isBookmarked = userMetaMap.value[poemId]?.isBookmarked ?: false
            repository.toggleBookmark(poemId, isBookmarked)
        }
    }

    fun saveNote(poemId: Int, note: String) {
        viewModelScope.launch {
            repository.saveNote(poemId, note)
        }
    }

    private fun recordRead(poemId: Int) {
        viewModelScope.launch {
            repository.markAsRead(poemId)
        }
    }

    fun playPoemAudio(poem: Poem, startVerseIndex: Int = 0) {
        audioPlayer.playPoem(poem, startVerseIndex)
    }

    fun playSingleVerseAudio(poem: Poem, verseIndex: Int) {
        audioPlayer.playSingleVerse(poem, verseIndex)
    }

    fun pauseAudio() = audioPlayer.pause()
    fun resumeAudio() = audioPlayer.resume()
    fun stopAudio() = audioPlayer.stop()
    fun setSpeechRate(rate: Float) = audioPlayer.setSpeechRate(rate)

    fun copyPoem(context: Context, poem: Poem) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = buildString {
            append("«${poem.title}»\n")
            if (poem.subTitle.isNotBlank()) append("${poem.subTitle}\n")
            append("من ديوان: وللشعر حلاوة كحلاوة السكر\n")
            append("للشاعر: د. مالك عبدالرحمن الرميمة\n\n")
            poem.verses.forEach { verse ->
                if (verse.secondHemistich.isNotBlank()) {
                    append("${verse.firstHemistich} ... ${verse.secondHemistich}\n")
                } else {
                    append("${verse.firstHemistich}\n")
                }
            }
            if (poem.dateString.isNotBlank()) append("\nالتاريخ: ${poem.dateString}")
            if (poem.dedication.isNotBlank()) append("\n${poem.dedication}")
        }
        val clip = ClipData.newPlainText("Poem", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "تم نسخ القصيدة بنجاح", Toast.LENGTH_SHORT).show()
    }

    fun sharePoem(context: Context, poem: Poem) {
        val text = buildString {
            append("«${poem.title}»\n")
            if (poem.subTitle.isNotBlank()) append("${poem.subTitle}\n")
            append("من ديوان: وللشعر حلاوة كحلاوة السكر\n")
            append("للشاعر: د. مالك عبدالرحمن الرميمة\n\n")
            poem.verses.forEach { verse ->
                if (verse.secondHemistich.isNotBlank()) {
                    append("${verse.firstHemistich} ... ${verse.secondHemistich}\n")
                } else {
                    append("${verse.firstHemistich}\n")
                }
            }
            if (poem.dateString.isNotBlank()) append("\nالتاريخ: ${poem.dateString}")
            if (poem.dedication.isNotBlank()) append("\n${poem.dedication}")
        }
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_TITLE, poem.title)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "مشاركة القصيدة")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
