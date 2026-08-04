package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.MainViewModel
import com.example.ui.screens.AdminNasScreen
import com.example.ui.screens.AiGuruScreen
import com.example.ui.screens.AiReaderModeScreen
import com.example.ui.screens.LibraryHomeScreen

enum class MainTab {
    LIBRARY,
    GURU_AI,
    READER_MODE,
    ADMIN_NAS
}

@Composable
fun MainAppNavigation(viewModel: MainViewModel) {
    var currentTab by remember { mutableStateOf(MainTab.LIBRARY) }

    val books by viewModel.books.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    val chatMessages by viewModel.chatMessages.collectAsState()
    val generatedQuiz by viewModel.generatedQuiz.collectAsState()

    val selectedBook by viewModel.selectedBook.collectAsState()
    val paragraphs by viewModel.simplifiedParagraphs.collectAsState()
    val highlightedIndex by viewModel.highlightedSentenceIndex.collectAsState()
    val isPlaying by viewModel.isTtsPlaying.collectAsState()
    val ttsRate by viewModel.ttsRate.collectAsState()
    val ttsGender by viewModel.ttsGender.collectAsState()

    val syncLogs by viewModel.syncLogs.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentTab == MainTab.LIBRARY,
                    onClick = { currentTab = MainTab.LIBRARY },
                    icon = { Icon(Icons.Default.LocalLibrary, contentDescription = "Perpustakaan") },
                    label = { Text("Katalog") },
                    modifier = Modifier.testTag("tab_library")
                )

                NavigationBarItem(
                    selected = currentTab == MainTab.GURU_AI,
                    onClick = { currentTab = MainTab.GURU_AI },
                    icon = { Icon(Icons.Default.Psychology, contentDescription = "GuruAI") },
                    label = { Text("GuruAI") },
                    modifier = Modifier.testTag("tab_guru_ai")
                )

                NavigationBarItem(
                    selected = currentTab == MainTab.READER_MODE,
                    onClick = { currentTab = MainTab.READER_MODE },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Baca AI") },
                    label = { Text("Baca AI") },
                    modifier = Modifier.testTag("tab_reader_mode")
                )

                NavigationBarItem(
                    selected = currentTab == MainTab.ADMIN_NAS,
                    onClick = { currentTab = MainTab.ADMIN_NAS },
                    icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin NAS") },
                    label = { Text("Admin") },
                    modifier = Modifier.testTag("tab_admin_nas")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainTab.LIBRARY -> LibraryHomeScreen(
                    viewModel = viewModel,
                    books = books,
                    searchQuery = searchQuery,
                    selectedCategory = selectedCategory,
                    onOpenReader = { book ->
                        viewModel.openBookForReading(book)
                        currentTab = MainTab.READER_MODE
                    }
                )

                MainTab.GURU_AI -> AiGuruScreen(
                    viewModel = viewModel,
                    chatMessages = chatMessages,
                    generatedQuiz = generatedQuiz,
                    books = books
                )

                MainTab.READER_MODE -> AiReaderModeScreen(
                    viewModel = viewModel,
                    selectedBook = selectedBook ?: books.firstOrNull(),
                    paragraphs = paragraphs,
                    highlightedIndex = highlightedIndex,
                    isPlaying = isPlaying,
                    ttsRate = ttsRate,
                    ttsGender = ttsGender
                )

                MainTab.ADMIN_NAS -> AdminNasScreen(
                    viewModel = viewModel,
                    syncLogs = syncLogs
                )
            }
        }
    }
}
