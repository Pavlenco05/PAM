package com.example.lab03.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lab03.data.model.FeedEntity
import com.example.lab03.data.model.FeedItemEntity
import com.example.lab03.ui.components.FeedItemCard
import com.example.lab03.ui.viewmodel.FeedUiState
import com.example.lab03.ui.viewmodel.FeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedListScreen(viewModel: FeedViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showAddFeedDialog by remember { mutableStateOf(false) }
    var showFeedSelector by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RSS Reader") },
                actions = {
                    IconButton(onClick = { viewModel.refreshCurrentFeed() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = { showAddFeedDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Feed")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is FeedUiState.Loading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                is FeedUiState.Empty -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Welcome! Add your first RSS feed")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Choose from samples or enter a custom URL", 
                                style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { showAddFeedDialog = true }) {
                                Text("Add Feed")
                            }
                        }
                    }
                }
                
                is FeedUiState.Success -> {
                    val state = uiState as FeedUiState.Success
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Current feed: ${state.selectedFeed?.title ?: "None"}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Box {
                            Button(onClick = { showFeedSelector = true }) {
                                Text("Change Feed")
                            }
                            
                            DropdownMenu(
                                expanded = showFeedSelector,
                                onDismissRequest = { showFeedSelector = false }
                            ) {
                                state.feeds.forEach { feed ->
                                    DropdownMenuItem(
                                        text = { Text(feed.title) },
                                        onClick = {
                                            viewModel.selectFeed(feed)
                                            showFeedSelector = false
                                        }
                                    )
                                    
                                    // Long press or additional menu item to delete feed
                                    DropdownMenuItem(
                                        text = { Text("Delete ${feed.title}") },
                                        onClick = {
                                            viewModel.deleteFeed(feed)
                                            showFeedSelector = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    LazyColumn {
                        items(state.feedItems) { item ->
                            FeedItemCard(
                                item = item,
                                onClick = {
                                    viewModel.markAsRead(item)
                                    if (item.link.isNotEmpty()) {
                                        openLink(context, item)
                                    } else {
                                        android.widget.Toast.makeText(
                                            context,
                                            "No link available for this item",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                onLongClick = {
                                    viewModel.toggleSavedStatus(item)
                                    android.widget.Toast.makeText(
                                        context,
                                        if (item.isSaved) "Item removed from saved" else "Item saved",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                }
                
                is FeedUiState.Error -> {
                    val errorState = uiState as FeedUiState.Error
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Error: ${errorState.message}",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row {
                                Button(onClick = { viewModel.refreshCurrentFeed() }) {
                                    Text("Retry")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = { showAddFeedDialog = true }) {
                                    Text("Add Feed")
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (showAddFeedDialog) {
            AddFeedDialog(
                onDismiss = { showAddFeedDialog = false },
                onAddFeed = { url ->
                    viewModel.addFeed(url)
                    showAddFeedDialog = false
                }
            )
        }
    }
}

@Composable
fun AddFeedDialog(
    onDismiss: () -> Unit,
    onAddFeed: (String) -> Unit
) {
    var feedUrl by remember { mutableStateOf("") }
    var showSampleFeeds by remember { mutableStateOf(false) }
    
    val sampleFeeds = listOf(
        "BBC News" to "https://feeds.bbci.co.uk/news/rss.xml",
        "CNN" to "https://rss.cnn.com/rss/edition.rss",
        "Reuters" to "https://feeds.reuters.com/reuters/topNews",
        "TechCrunch" to "https://techcrunch.com/feed/",
        "The Verge" to "https://www.theverge.com/rss/index.xml",
        "Reddit Programming" to "https://www.reddit.com/r/programming/.rss",
        "Android Developers" to "https://android-developers.googleblog.com/feeds/posts/default",
        "YAM News (Romanian)" to "https://news.yam.md/ro/rss"
    )
    
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add RSS Feed") },
        text = {
            Column {
                Text("Enter RSS feed URL or choose a sample:")
                Spacer(modifier = Modifier.height(8.dp))
                
                if (showSampleFeeds) {
                    sampleFeeds.forEach { (name, url) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    feedUrl = url
                                    showSampleFeeds = false
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(name, modifier = Modifier.weight(1f))
                            Text(url, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = feedUrl,
                        onValueChange = { feedUrl = it },
                        label = { Text("Feed URL") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { if (feedUrl.isNotEmpty()) onAddFeed(feedUrl) }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showSampleFeeds = !showSampleFeeds },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (showSampleFeeds) "Enter Custom URL" else "Choose Sample Feed")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAddFeed(feedUrl) },
                enabled = feedUrl.isNotEmpty()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun openLink(context: android.content.Context, item: FeedItemEntity) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.link))
    context.startActivity(intent)
}
