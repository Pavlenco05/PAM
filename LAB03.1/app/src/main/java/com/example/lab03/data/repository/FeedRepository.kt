package com.example.lab03.data.repository

import com.example.lab03.data.local.FeedDatabaseHelper
import com.example.lab03.data.model.FeedEntity
import com.example.lab03.data.model.FeedItemEntity
import com.example.lab03.data.model.Item
import com.example.lab03.data.remote.RssService
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class FeedRepository(
    private val rssService: RssService,
    private val dbHelper: FeedDatabaseHelper
) {
    fun getAllFeeds(): Flow<List<FeedEntity>> {
        return dbHelper.getAllFeeds()
    }

    fun getItemsByFeedUrl(feedUrl: String): Flow<List<FeedItemEntity>> {
        return dbHelper.getItemsByFeedUrl(feedUrl)
    }

    fun getSavedItems(): Flow<List<FeedItemEntity>> {
        return dbHelper.getSavedItems()
    }

    suspend fun refreshFeed(feedUrl: String): Result<Unit> {
        return try {
            android.util.Log.d("FeedRepository", "Refreshing feed: $feedUrl")
            val response = rssService.getFeed(feedUrl)
            android.util.Log.d("FeedRepository", "Response code: ${response.code()}")
            
            if (response.isSuccessful) {
                val rssFeed = response.body()
                android.util.Log.d("FeedRepository", "Feed received: ${rssFeed != null}")
                
                if (rssFeed == null) {
                    return Result.failure(Exception("Empty response body from $feedUrl"))
                }
                
                try {
                    // Save or update feed
                    val feedEntity = FeedEntity(
                        url = feedUrl,
                        title = rssFeed.channel.title.ifEmpty { "Unnamed Feed" },
                        description = rssFeed.channel.description.ifEmpty { "No description" },
                        lastUpdated = System.currentTimeMillis()
                    )
                    dbHelper.insertFeed(feedEntity)
                    android.util.Log.d("FeedRepository", "Feed saved: ${feedEntity.title}")

                    // Save feed items
                    val items = rssFeed.channel.items
                    android.util.Log.d("FeedRepository", "Items count: ${items.size}")
                    
                    val feedItems = items.map { item ->
                        mapToFeedItemEntity(item, feedUrl)
                    }
                    
                    if (feedItems.isNotEmpty()) {
                        dbHelper.insertFeedItems(feedItems)
                        android.util.Log.d("FeedRepository", "Items saved: ${feedItems.size}")
                    }
                    
                    Result.success(Unit)
                } catch (e: Exception) {
                    android.util.Log.e("FeedRepository", "Error processing feed data", e)
                    Result.failure(e)
                }
            } else {
                android.util.Log.e("FeedRepository", "Failed to fetch feed: ${response.code()}")
                val errorMessage = when (response.code()) {
                    404 -> "Feed not found: $feedUrl"
                    403 -> "Access denied to feed: $feedUrl"
                    500 -> "Server error for feed: $feedUrl"
                    else -> "Failed to fetch feed (${response.code()}): $feedUrl"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            android.util.Log.e("FeedRepository", "Exception refreshing feed", e)
            val errorMessage = when {
                e.message?.contains("Unable to resolve host") == true -> "Cannot connect to server. Check internet connection."
                e.message?.contains("timeout") == true -> "Connection timeout. Please try again."
                else -> "Network error: ${e.message ?: "Unknown error"}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    suspend fun addFeed(url: String): Result<Unit> {
        return refreshFeed(url)
    }

    suspend fun deleteFeed(feed: FeedEntity) {
        dbHelper.deleteFeed(feed.url)
        dbHelper.deleteFeedItems(feed.url)
    }

    suspend fun toggleSavedStatus(item: FeedItemEntity) {
        dbHelper.updateFeedItem(item.copy(isSaved = !item.isSaved))
    }

    suspend fun markAsRead(item: FeedItemEntity) {
        if (!item.isRead) {
            dbHelper.updateFeedItem(item.copy(isRead = true))
        }
    }

    private fun mapToFeedItemEntity(item: Item, feedUrl: String): FeedItemEntity {
        return FeedItemEntity(
            guid = item.guid.ifEmpty { item.link.ifEmpty { UUID.randomUUID().toString() } },
            feedUrl = feedUrl,
            title = item.title,
            link = item.link,
            description = item.description,
            pubDate = item.pubDate,
            isRead = false,
            isSaved = false
        )
    }
}
