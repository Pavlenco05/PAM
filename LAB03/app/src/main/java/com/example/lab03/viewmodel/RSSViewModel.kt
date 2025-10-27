package com.example.lab03.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab03.data.RSSFeed
import com.example.lab03.data.RSSPost
import com.example.lab03.repository.RSSRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RSSViewModel(private val repository: RSSRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RSSUiState())
    val uiState: StateFlow<RSSUiState> = _uiState.asStateFlow()
    
    val feeds = repository.getAllFeeds()
    
    fun addFeed(url: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                val feed = repository.addFeed(url)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, 
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }
    
    fun loadPosts(feedId: Long) {
        _uiState.value = _uiState.value.copy(selectedFeedId = feedId)
    }
    
    fun deleteFeed(feed: RSSFeed) {
        viewModelScope.launch {
            try {
                repository.deleteFeed(feed)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to delete feed"
                )
            }
        }
    }
    
    fun refreshFeed(feed: RSSFeed) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                repository.refreshFeed(feed)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to refresh feed"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class RSSUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFeedId: Long? = null
)
