package com.example.lab03.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lab03.data.model.FeedEntity
import com.example.lab03.data.model.FeedItemEntity
import com.example.lab03.data.repository.FeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class FeedViewModel(private val repository: FeedRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState

    private val _selectedFeed = MutableStateFlow<FeedEntity?>(null)
    private val _feedItems = MutableStateFlow<List<FeedItemEntity>>(emptyList())
    private val _feeds = MutableStateFlow<List<FeedEntity>>(emptyList())
    private val _errorMessage = MutableStateFlow<String?>(null)

    init {
        loadFeeds()
        // Don't auto-load feed - let user choose for faster startup
    }

    private fun loadFeeds() {
        viewModelScope.launch {
            repository.getAllFeeds()
                .catch { e ->
                    _errorMessage.value = e.message
                    _uiState.value = FeedUiState.Error(e.message ?: "Unknown error")
                }
                .collect { feeds ->
                    _feeds.value = feeds
                    if (feeds.isEmpty()) {
                        _uiState.value = FeedUiState.Empty
                    } else {
                        // Select the first feed if none is selected
                        if (_selectedFeed.value == null) {
                            selectFeed(feeds.first())
                        } else {
                            // Refresh the current feed items
                            _selectedFeed.value?.let { loadFeedItems(it.url) }
                        }
                    }
                }
        }
    }

    private fun loadFeedItems(feedUrl: String) {
        viewModelScope.launch {
            repository.getItemsByFeedUrl(feedUrl)
                .catch { e ->
                    _errorMessage.value = e.message
                    _uiState.value = FeedUiState.Error(e.message ?: "Unknown error")
                }
                .collect { items ->
                    _feedItems.value = items
                    updateUiState()
                }
        }
    }

    private fun updateUiState() {
        val feeds = _feeds.value
        val selectedFeed = _selectedFeed.value
        val items = _feedItems.value

        _uiState.value = if (feeds.isEmpty()) {
            FeedUiState.Empty
        } else {
            FeedUiState.Success(
                feeds = feeds,
                selectedFeed = selectedFeed,
                feedItems = items
            )
        }
    }

    fun selectFeed(feed: FeedEntity) {
        _selectedFeed.value = feed
        loadFeedItems(feed.url)
        refreshCurrentFeed()
    }

    fun addFeed(url: String) {
        viewModelScope.launch {
            _uiState.value = FeedUiState.Loading
            val result = repository.addFeed(url)
            
            result.fold(
                onSuccess = {
                    // Feed added successfully, it will be loaded in the flow
                },
                onFailure = { e ->
                    _errorMessage.value = e.message
                    // If XML parsing fails, suggest trying a different feed
                    val errorMessage = if (e.message?.contains("XML") == true || e.message?.contains("format") == true) {
                        "${e.message}\n\nTry selecting a different feed from the samples."
                    } else {
                        e.message ?: "Failed to add feed"
                    }
                    _uiState.value = FeedUiState.Error(errorMessage)
                }
            )
        }
    }
    
    // For initial feed loading with fallback
    private suspend fun addFeedWithResult(url: String): Result<Unit> {
        return repository.addFeed(url)
    }

    fun refreshCurrentFeed() {
        val currentFeed = _selectedFeed.value ?: return
        viewModelScope.launch {
            val result = repository.refreshFeed(currentFeed.url)
            result.onFailure { e ->
                _errorMessage.value = e.message
            }
        }
    }

    fun deleteFeed(feed: FeedEntity) {
        viewModelScope.launch {
            repository.deleteFeed(feed)
            if (_selectedFeed.value == feed) {
                _selectedFeed.value = _feeds.value.firstOrNull { it != feed }
            }
        }
    }

    fun toggleSavedStatus(item: FeedItemEntity) {
        viewModelScope.launch {
            repository.toggleSavedStatus(item)
        }
    }

    fun markAsRead(item: FeedItemEntity) {
        viewModelScope.launch {
            repository.markAsRead(item)
        }
    }

    class Factory(private val repository: FeedRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
                return FeedViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

sealed class FeedUiState {
    data object Loading : FeedUiState()
    data object Empty : FeedUiState()
    data class Success(
        val feeds: List<FeedEntity>,
        val selectedFeed: FeedEntity?,
        val feedItems: List<FeedItemEntity>
    ) : FeedUiState()
    data class Error(val message: String) : FeedUiState()
}
