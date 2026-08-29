package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PoemData
import com.example.data.model.PoemCategory
import com.example.ui.PoemViewModel
import com.example.ui.Screen
import com.example.ui.components.PoemCard
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.ParchmentText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: PoemViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val poems by viewModel.filteredPoems.collectAsState()
    val userMeta by viewModel.userMetaMap.collectAsState()
    val latestBookmark by viewModel.latestBookmark.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val listState = rememberLazyListState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "وللشعر حلاوة",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "ديوان الدكتور مالك عبدالرحمن الرميمة",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.Search) },
                    modifier = Modifier.testTag("search_action_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "بحث في الديوان",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.BookInfo) },
                    modifier = Modifier.testTag("book_info_action_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "عن الكتاب والشاعر",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        )

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(bottom = 90.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Hero Banner
            item {
                HeroBanner(
                    totalPoems = PoemData.allPoems.size,
                    onReadBookClick = {
                        viewModel.navigateTo(Screen.PoemDetail(1))
                    },
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Daily Verse / Quote of the Day
            item {
                val (poem, verseText) = viewModel.dailyVerse
                DailyVerseCard(
                    poemTitle = poem.title,
                    verseText = verseText,
                    onReadPoem = { viewModel.navigateTo(Screen.PoemDetail(poem.id)) },
                    onPlayAudio = { viewModel.playPoemAudio(poem) },
                    onCopy = { viewModel.copyPoem(context, poem) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Quick Resume from Bookmark
            if (latestBookmark != null) {
                val bookmarkedPoem = PoemData.getPoemById(latestBookmark!!.poemId)
                if (bookmarkedPoem != null) {
                    item {
                        ResumeCard(
                            poemTitle = bookmarkedPoem.title,
                            pageNumber = bookmarkedPoem.pageNumber,
                            onResumeClick = {
                                viewModel.navigateTo(Screen.PoemDetail(bookmarkedPoem.id))
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Categories Filter Bar
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "فهرس وتبويب القصائد:",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PoemCategory.entries.forEach { category ->
                            val isSelected = category == selectedCategory
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.onCategorySelected(category) },
                                label = {
                                    Text(
                                        text = "${category.arabicName} (${if (category == PoemCategory.ALL) PoemData.allPoems.size else PoemData.getPoemsByCategory(category).size})",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }

            // Poems List Count Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "قائمة القصائد (${poems.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Poem Items
            items(poems, key = { it.id }) { poem ->
                val meta = userMeta[poem.id]
                PoemCard(
                    poem = poem,
                    isFavorite = meta?.isFavorite ?: false,
                    isBookmarked = meta?.isBookmarked ?: false,
                    onPoemClick = { viewModel.navigateTo(Screen.PoemDetail(poem.id)) },
                    onFavoriteToggle = { viewModel.toggleFavorite(poem.id) },
                    onBookmarkToggle = { viewModel.toggleBookmark(poem.id) },
                    onPlayClick = { viewModel.playPoemAudio(poem) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun HeroBanner(
    totalPoems: Int,
    onReadBookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            EmeraldDark,
                            EmeraldPrimary
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Gold Calligraphic Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = GoldAccent.copy(alpha = 0.2f),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "✦ ديوان شعري معتمد وموثق ✦",
                        color = GoldAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                Text(
                    text = "وللشعر حلاوة كحلاوة السكر",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "تأليف الشاعر الدكتور / مالك عبدالرحمن الرميمة",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InfoStat(number = "$totalPoems", label = "قصيدة ونثر")
                    InfoStat(number = "190", label = "صفحة كاملة")
                    InfoStat(number = "6", label = "أبواب وفنون")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldAccent,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onReadBookClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = EmeraldDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "بدء القراءة من الصفحة الأولى",
                            color = EmeraldDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoStat(number: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = number,
            color = GoldAccent,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 11.sp
        )
    }
}

@Composable
private fun DailyVerseCard(
    poemTitle: String,
    verseText: String,
    onReadPoem: () -> Unit,
    onPlayAudio: () -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = GoldContainer.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🌟 بيت اليوم المختارة",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDark
                )
                Row {
                    IconButton(onClick = onPlayAudio, modifier = Modifier.size(30.dp)) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "استماع",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onCopy, modifier = Modifier.size(30.dp)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "نسخ",
                            tint = EmeraldDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "« $verseText »",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = ParchmentText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onReadPoem() }
                    .padding(vertical = 6.dp)
            )

            Text(
                text = "من قصيدة: $poemTitle",
                fontSize = 11.sp,
                color = EmeraldPrimary,
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ResumeCard(
    poemTitle: String,
    pageNumber: Int,
    onResumeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onResumeClick() },
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "متابعة القراءة من آخر علامة مرجعية:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "$poemTitle (ص $pageNumber)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Text(
                text = "انتقال ❯",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
