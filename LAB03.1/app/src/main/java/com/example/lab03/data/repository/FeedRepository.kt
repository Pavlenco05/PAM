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
            val response = rssService.getFeed(feedUrl)
            
            if (response.isSuccessful) {
                val rssFeed = response.body()
                
                if (rssFeed == null) {
                    return Result.failure(Exception("Empty response body from $feedUrl"))
                }
                
                try {
                    // Save or update feed (optimized - single DB call)
                    val feedEntity = FeedEntity(
                        url = feedUrl,
                        title = rssFeed.channel.title.ifEmpty { "Unnamed Feed" },
                        description = rssFeed.channel.description.ifEmpty { "No description" },
                        lastUpdated = System.currentTimeMillis()
                    )
                    dbHelper.insertFeed(feedEntity)

                    // Save feed items (optimized - batch insert)
                    val items = rssFeed.channel.items ?: emptyList()
                    
                    if (items.isNotEmpty()) {
                        val feedItems = items.mapNotNull { item ->
                            try {
                                mapToFeedItemEntity(item, feedUrl)
                            } catch (e: Exception) {
                                null // Skip invalid items silently for speed
                            }
                        }
                        if (feedItems.isNotEmpty()) {
                            dbHelper.insertFeedItems(feedItems)
                        }
                    }
                    
                    Result.success(Unit)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Feed not found"
                    403 -> "Access denied"
                    500 -> "Server error"
                    else -> "Failed to fetch feed (${response.code()})"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            android.util.Log.e("FeedRepository", "Error refreshing feed: ${e.message}", e)
            val errorMessage = when {
                e.message?.contains("Unable to resolve host") == true -> "Check internet connection"
                e.message?.contains("timeout") == true -> "Request timeout"
                e.message?.contains("XMLStreamException") == true -> "Invalid XML format - trying alternative feeds"
                e.message?.contains("parseError") == true -> "XML parsing error - feed may be corrupted"
                e.message?.contains("whitespace content") == true -> "XML format issue - trying to fix..."
                e.message?.contains("Invalid XML format") == true -> "Feed has malformed XML - try a different feed"
                e is org.simpleframework.xml.core.PersistenceException -> "XML parsing failed - feed format not supported"
                else -> "Error: ${e.message ?: "Unknown error"}"
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
            title = item.title.ifEmpty { "Untitled" },
            link = item.link.ifEmpty { "" },
            description = item.description.ifEmpty { "No description available" },
            pubDate = item.pubDate.ifEmpty { "Unknown date" },
            isRead = false,
            isSaved = false
        )
    }
}
