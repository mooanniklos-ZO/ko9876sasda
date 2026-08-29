package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PoemData
import com.example.ui.PoemViewModel
import com.example.ui.Screen
import com.example.ui.components.PoemCard
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RoseAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: PoemViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val userMeta by viewModel.userMetaMap.collectAsState()

    val favoritePoems = remember(userMeta) {
        userMeta.filter { it.value.isFavorite }.keys.mapNotNull { PoemData.getPoemById(it) }
    }

    val bookmarkedPoems = remember(userMeta) {
        userMeta.filter { it.value.isBookmarked }.keys.mapNotNull { PoemData.getPoemById(it) }
    }

    val notedPoems = remember(userMeta) {
        userMeta.filter { it.value.note.isNotBlank() }.mapNotNull { entry ->
            PoemData.getPoemById(entry.key)?.let { poem -> Pair(poem, entry.value.note) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("المفضلة والعلامات", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.Library) },
                    modifier = Modifier.testTag("favorites_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        // Tabs Row
        PrimaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("المفضلة (${favoritePoems.size})", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.Favorite, contentDescription = null, tint = if (selectedTab == 0) RoseAccent else Color.Gray) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("العلامات (${bookmarkedPoems.size})", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.Bookmark, contentDescription = null, tint = if (selectedTab == 1) GoldAccent else Color.Gray) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("الملاحظات (${notedPoems.size})", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.EditNote, contentDescription = null) }
            )
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                if (favoritePoems.isEmpty()) {
                    EmptyPlaceholder(
                        icon = Icons.Default.Favorite,
                        title = "لا توجد قصائد في المفضلة بعد",
                        subtitle = "اضغط على رمز القلب في أي قصيدة لإضافتها إلى قائمتك المفضلة للرجوع إليها سريعاً"
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(favoritePoems, key = { it.id }) { poem ->
                            val meta = userMeta[poem.id]
                            PoemCard(
                                poem = poem,
                                isFavorite = true,
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
            1 -> {
                if (bookmarkedPoems.isEmpty()) {
                    EmptyPlaceholder(
                        icon = Icons.Default.Bookmark,
                        title = "لا توجد إشارات مرجعية محفوظة",
                        subtitle = "يمكنك حفظ موضع القراءة بالضغط على رمز الإشارة المرجعية في أي قصيدة أو صفحة"
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(bookmarkedPoems, key = { it.id }) { poem ->
                            val meta = userMeta[poem.id]
                            PoemCard(
                                poem = poem,
                                isFavorite = meta?.isFavorite ?: false,
                                isBookmarked = true,
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
            2 -> {
                if (notedPoems.isEmpty()) {
                    EmptyPlaceholder(
                        icon = Icons.Default.EditNote,
                        title = "لا توجد ملاحظات أو تأملات مسجلة",
                        subtitle = "عند قراءة أي قصيدة يمكنك تدوين خواطرك واستنباطاتك وستظهر هنا جميعها مرتبة"
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(notedPoems, key = { it.first.id }) { (poem, note) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { viewModel.navigateTo(Screen.PoemDetail(poem.id)) },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = poem.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "ص ${poem.pageNumber}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = note,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyPlaceholder(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
