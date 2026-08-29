package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PoemData
import com.example.ui.PoemViewModel
import com.example.ui.Screen
import com.example.ui.components.AudioMiniPlayer
import com.example.ui.screens.BookInfoScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.PoemDetailScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.HalawatAlSheirTheme

class MainActivity : ComponentActivity() {

    private val viewModel: PoemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HalawatAlSheirTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    PoemAppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun PoemAppContent(viewModel: PoemViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val audioState by viewModel.audioState.collectAsState()
    val playingPoem = audioState.currentPoemId?.let { PoemData.getPoemById(it) }

    // System Back Handler
    BackHandler(enabled = currentScreen !is Screen.Library) {
        viewModel.navigateTo(Screen.Library)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentScreen !is Screen.PoemDetail) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.testTag("main_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = currentScreen is Screen.Library,
                        onClick = { viewModel.navigateTo(Screen.Library) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen is Screen.Library) Icons.Filled.AutoStories else Icons.Outlined.AutoStories,
                                contentDescription = "الديوان"
                            )
                        },
                        label = { Text("الديوان", fontSize = 12.sp, fontWeight = if (currentScreen is Screen.Library) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_library")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.Search,
                        onClick = { viewModel.navigateTo(Screen.Search) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen is Screen.Search) Icons.Filled.Search else Icons.Outlined.Search,
                                contentDescription = "البحث"
                            )
                        },
                        label = { Text("البحث", fontSize = 12.sp, fontWeight = if (currentScreen is Screen.Search) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_search")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.Favorites,
                        onClick = { viewModel.navigateTo(Screen.Favorites) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen is Screen.Favorites) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "المفضلة"
                            )
                        },
                        label = { Text("المفضلة", fontSize = 12.sp, fontWeight = if (currentScreen is Screen.Favorites) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_favorites")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.BookInfo,
                        onClick = { viewModel.navigateTo(Screen.BookInfo) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen is Screen.BookInfo) Icons.Filled.Info else Icons.Outlined.Info,
                                contentDescription = "عن الكتاب"
                            )
                        },
                        label = { Text("عن الكتاب", fontSize = 12.sp, fontWeight = if (currentScreen is Screen.BookInfo) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_info")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Library -> LibraryScreen(viewModel = viewModel)
                is Screen.Search -> SearchScreen(viewModel = viewModel)
                is Screen.Favorites -> FavoritesScreen(viewModel = viewModel)
                is Screen.BookInfo -> BookInfoScreen(viewModel = viewModel)
                is Screen.PoemDetail -> PoemDetailScreen(poemId = screen.poemId, viewModel = viewModel)
            }

            // Floating Mini Player when reciting audio (only in non-detail screens)
            if (currentScreen !is Screen.PoemDetail && playingPoem != null) {
                AudioMiniPlayer(
                    audioState = audioState,
                    currentPoem = playingPoem,
                    onPlayPause = {
                        if (audioState.isPlaying) viewModel.pauseAudio() else viewModel.resumeAudio()
                    },
                    onNext = { viewModel.audioPlayer.nextVerse() },
                    onPrevious = { viewModel.audioPlayer.previousVerse() },
                    onStop = { viewModel.stopAudio() },
                    onSpeedChange = { viewModel.setSpeechRate(it) },
                    onOpenPoem = { viewModel.navigateTo(Screen.PoemDetail(it)) },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}
