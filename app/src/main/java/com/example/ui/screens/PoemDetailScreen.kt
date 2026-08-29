package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PoemData
import com.example.data.model.Poem
import com.example.data.model.ReadingTheme
import com.example.data.model.Verse
import com.example.ui.PoemViewModel
import com.example.ui.Screen
import com.example.ui.components.PoemNoteDialog
import com.example.ui.components.ReadingSettingsDialog
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldThemeBg
import com.example.ui.theme.EmeraldThemeBorder
import com.example.ui.theme.EmeraldThemeCard
import com.example.ui.theme.EmeraldThemeSubtext
import com.example.ui.theme.EmeraldThemeText
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MidnightThemeBg
import com.example.ui.theme.MidnightThemeBorder
import com.example.ui.theme.MidnightThemeCard
import com.example.ui.theme.MidnightThemeSubtext
import com.example.ui.theme.MidnightThemeText
import com.example.ui.theme.ParchmentBg
import com.example.ui.theme.ParchmentBorder
import com.example.ui.theme.ParchmentCard
import com.example.ui.theme.ParchmentSubtext
import com.example.ui.theme.ParchmentText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RoseAccent
import com.example.ui.theme.RoseThemeBg
import com.example.ui.theme.RoseThemeBorder
import com.example.ui.theme.RoseThemeCard
import com.example.ui.theme.RoseThemeSubtext
import com.example.ui.theme.RoseThemeText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoemDetailScreen(
    poemId: Int,
    viewModel: PoemViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val poem = PoemData.getPoemById(poemId) ?: PoemData.allPoems.first()
    val allPoems = PoemData.allPoems
    val currentIndex = allPoems.indexOfFirst { it.id == poem.id }

    val userMeta by viewModel.userMetaMap.collectAsState()
    val meta = userMeta[poem.id]
    val readingSettings by viewModel.readingSettings.collectAsState()
    val audioState by viewModel.audioState.collectAsState()

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showNoteDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Auto-scroll to active reciting verse
    LaunchedEffect(audioState.currentVerseIndex) {
        if (audioState.isPlaying && audioState.currentPoemId == poem.id) {
            val targetIndex = (audioState.currentVerseIndex + 2).coerceIn(0, poem.verses.size + 2)
            listState.animateScrollToItem(targetIndex)
        }
    }

    // Colors according to theme
    val (canvasBg, cardBg, borderColor, textColor, subtextColor) = when (readingSettings.theme) {
        ReadingTheme.PARCHMENT -> ThemeColors(ParchmentBg, ParchmentCard, ParchmentBorder, ParchmentText, ParchmentSubtext)
        ReadingTheme.EMERALD -> ThemeColors(EmeraldThemeBg, EmeraldThemeCard, EmeraldThemeBorder, EmeraldThemeText, EmeraldThemeSubtext)
        ReadingTheme.MIDNIGHT -> ThemeColors(MidnightThemeBg, MidnightThemeCard, MidnightThemeBorder, MidnightThemeText, MidnightThemeSubtext)
        ReadingTheme.ROSE -> ThemeColors(RoseThemeBg, RoseThemeCard, RoseThemeBorder, RoseThemeText, RoseThemeSubtext)
        ReadingTheme.CLASSIC_LIGHT -> ThemeColors(PureWhite, Color(0xFFF9F9F9), Color(0xFFE0E0E0), Color(0xFF212121), Color(0xFF757575))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(canvasBg)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = poem.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        color = Color.White
                    )
                    Text(
                        text = "صفحة ${poem.pageNumber} من 190 • ${poem.category.arabicName}",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.Library) },
                    modifier = Modifier.testTag("poem_detail_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = Color.White
                    )
                }
            },
            actions = {
                // Settings
                IconButton(onClick = { showSettingsDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = "خيارات القراءة",
                        tint = Color.White
                    )
                }
                // Notes
                IconButton(onClick = { showNoteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = "ملاحظات",
                        tint = if (meta?.note?.isNotBlank() == true) GoldAccent else Color.White
                    )
                }
                // Bookmark
                IconButton(onClick = { viewModel.toggleBookmark(poem.id) }) {
                    Icon(
                        imageVector = if (meta?.isBookmarked == true) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "علامة مرجعية",
                        tint = if (meta?.isBookmarked == true) GoldAccent else Color.White
                    )
                }
                // Favorite
                IconButton(onClick = { viewModel.toggleFavorite(poem.id) }) {
                    Icon(
                        imageVector = if (meta?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "مفضلة",
                        tint = if (meta?.isFavorite == true) RoseAccent else Color.White
                    )
                }
                // Share
                IconButton(onClick = { viewModel.sharePoem(context, poem) }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "مشاركة",
                        tint = Color.White
                    )
                }
                // Copy
                IconButton(onClick = { viewModel.copyPoem(context, poem) }) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "نسخ القصيدة",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = EmeraldDark
            )
        )

        // Main Poem Content (LazyColumn)
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Header Ornament & Title
            item {
                PoemHeaderCard(
                    poem = poem,
                    cardBg = cardBg,
                    borderColor = borderColor,
                    textColor = textColor,
                    subtextColor = subtextColor,
                    showFrame = readingSettings.showDecorativeFrame
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Verses List
            itemsIndexed(poem.verses, key = { index, _ -> "${poem.id}_$index" }) { index, verse ->
                val isReciting = audioState.isPlaying && audioState.currentPoemId == poem.id && audioState.currentVerseIndex == index
                VerseItemCard(
                    verse = verse,
                    verseIndex = index,
                    fontSize = readingSettings.fontSizeSp,
                    isReciting = isReciting,
                    showNumber = readingSettings.showVerseNumbers,
                    cardBg = cardBg,
                    borderColor = borderColor,
                    textColor = textColor,
                    subtextColor = subtextColor,
                    onPlayVerse = { viewModel.playSingleVerseAudio(poem, index) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Dedication & Notes Footer
            item {
                PoemFooterCard(
                    poem = poem,
                    userNote = meta?.note ?: "",
                    cardBg = cardBg,
                    borderColor = borderColor,
                    textColor = textColor,
                    subtextColor = subtextColor,
                    onAddNoteClick = { showNoteDialog = true }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Bottom Navigation & Recitation Bar
        BottomAppBar(
            containerColor = EmeraldDark,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Poem Button
                IconButton(
                    onClick = {
                        if (currentIndex > 0) {
                            val prevPoem = allPoems[currentIndex - 1]
                            viewModel.navigateTo(Screen.PoemDetail(prevPoem.id))
                        }
                    },
                    enabled = currentIndex > 0,
                    modifier = Modifier.testTag("prev_poem_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "القصيدة السابقة",
                        tint = if (currentIndex > 0) Color.White else Color.White.copy(alpha = 0.3f)
                    )
                }

                // Audio Recite Button in Center
                val isThisPoemPlaying = audioState.isPlaying && audioState.currentPoemId == poem.id
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = GoldAccent,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            if (isThisPoemPlaying) {
                                viewModel.pauseAudio()
                            } else {
                                viewModel.playPoemAudio(poem)
                            }
                        }
                        .testTag("play_full_poem_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isThisPoemPlaying) Icons.Default.Pause else Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = EmeraldDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isThisPoemPlaying) "إيقاف التلاوة" else "استماع كامل للقصيدة",
                            color = EmeraldDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Next Poem Button
                IconButton(
                    onClick = {
                        if (currentIndex < allPoems.size - 1) {
                            val nextPoem = allPoems[currentIndex + 1]
                            viewModel.navigateTo(Screen.PoemDetail(nextPoem.id))
                        }
                    },
                    enabled = currentIndex < allPoems.size - 1,
                    modifier = Modifier.testTag("next_poem_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "القصيدة التالية",
                        tint = if (currentIndex < allPoems.size - 1) Color.White else Color.White.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }

    // Dialogs
    if (showSettingsDialog) {
        ReadingSettingsDialog(
            settings = readingSettings,
            onSettingsChanged = { viewModel.updateReadingSettings { _ -> it } },
            onDismiss = { showSettingsDialog = false }
        )
    }

    if (showNoteDialog) {
        PoemNoteDialog(
            initialNote = meta?.note ?: "",
            poemTitle = poem.title,
            onSaveNote = { viewModel.saveNote(poem.id, it) },
            onDismiss = { showNoteDialog = false }
        )
    }
}

@Composable
private fun PoemHeaderCard(
    poem: Poem,
    cardBg: Color,
    borderColor: Color,
    textColor: Color,
    subtextColor: Color,
    showFrame: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (showFrame) Modifier.border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
                else Modifier
            ),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "﷽",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = poem.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )

            if (poem.subTitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = poem.subTitle,
                    fontSize = 14.sp,
                    color = subtextColor,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = EmeraldPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "صفحة ${poem.pageNumber}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GoldAccent.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = poem.category.arabicName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = GoldAccent,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "❖ ─── ✦ ─── ❖",
                color = GoldAccent.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun VerseItemCard(
    verse: Verse,
    verseIndex: Int,
    fontSize: Float,
    isReciting: Boolean,
    showNumber: Boolean,
    cardBg: Color,
    borderColor: Color,
    textColor: Color,
    subtextColor: Color,
    onPlayVerse: () -> Unit
) {
    val animatedBg by animateColorAsState(
        targetValue = if (isReciting) GoldAccent.copy(alpha = 0.25f) else cardBg,
        label = "verse_bg"
    )

    val animatedBorder by animateColorAsState(
        targetValue = if (isReciting) GoldAccent else borderColor.copy(alpha = 0.4f),
        label = "verse_border"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(if (isReciting) 2.dp else 1.dp, animatedBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = animatedBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showNumber) {
                    Surface(
                        shape = CircleShape,
                        color = if (isReciting) GoldAccent else borderColor.copy(alpha = 0.3f),
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${verse.order}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isReciting) EmeraldDark else textColor
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                IconButton(
                    onClick = onPlayVerse,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isReciting) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "استماع للبيت",
                        tint = if (isReciting) EmeraldPrimary else subtextColor.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // First Hemistich (الصدر)
            Text(
                text = verse.firstHemistich,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                textAlign = TextAlign.Center,
                lineHeight = (fontSize * 1.5).sp,
                modifier = Modifier.fillMaxWidth()
            )

            // Second Hemistich (العجز)
            if (verse.secondHemistich.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "✧",
                    color = GoldAccent.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = verse.secondHemistich,
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    lineHeight = (fontSize * 1.5).sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun PoemFooterCard(
    poem: Poem,
    userNote: String,
    cardBg: Color,
    borderColor: Color,
    textColor: Color,
    subtextColor: Color,
    onAddNoteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, borderColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            if (poem.dateString.isNotBlank()) {
                Text(
                    text = "تاريخ النظم: ${poem.dateString}",
                    fontSize = 12.sp,
                    color = subtextColor
                )
            }

            if (poem.dedication.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = poem.dedication,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldPrimary
                )
            }

            if (poem.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "شرح وهوامش: ${poem.notes}",
                    fontSize = 12.sp,
                    color = subtextColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (userNote.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldAccent.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "ملاحظتك المحفوظة:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = userNote,
                            fontSize = 13.sp,
                            color = textColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            Text(
                text = if (userNote.isBlank()) "+ إضافة ملاحظة أو خاطرة لهذه القصيدة" else "✎ تعديل الملاحظة",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { onAddNoteClick() }
                    .padding(vertical = 4.dp)
            )
        }
    }
}

private data class ThemeColors(
    val bg: Color,
    val card: Color,
    val border: Color,
    val text: Color,
    val subtext: Color
)
