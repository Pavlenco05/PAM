package com.example.lab03

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab03.data.RSSDatabase
import com.example.lab03.repository.RSSRepository
import com.example.lab03.service.RSSService
import com.example.lab03.ui.screens.AddFeedDialog
import com.example.lab03.ui.screens.FeedListScreen
import com.example.lab03.ui.screens.PostsScreen
import com.example.lab03.ui.theme.LAB03Theme
import com.example.lab03.viewmodel.RSSViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LAB03Theme {
                RSSReaderApp()
            }
        }
    }
}

@Composable
fun RSSReaderApp() {
    val database = RSSDatabase.getDatabase(LocalContext.current)
    val repository = RSSRepository(database.rssDao(), RSSService())
    val viewModel: RSSViewModel = viewModel { RSSViewModel(repository) }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFeedId by remember { mutableStateOf<Long?>(null) }
    
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when {
                selectedFeedId != null -> {
                    PostsScreen(
                        feedId = selectedFeedId!!,
                        repository = repository,
                        onBackClick = { selectedFeedId = null }
                    )
                }
                else -> {
                    FeedListScreen(
                        viewModel = viewModel,
                        onFeedClick = { feedId -> selectedFeedId = feedId },
                        onAddFeed = { showAddDialog = true }
                    )
                }
            }
            
            AddFeedDialog(
                isVisible = showAddDialog,
                onDismiss = { showAddDialog = false },
                onAddFeed = { url -> viewModel.addFeed(url) }
            )
        }
    }
}