package com.example.lab03.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RSSDao {
    @Query("SELECT * FROM rss_feeds ORDER BY addedDate DESC")
    fun getAllFeeds(): Flow<List<RSSFeed>>

    @Query("SELECT * FROM rss_feeds WHERE id = :feedId")
    suspend fun getFeedById(feedId: Long): RSSFeed?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeed(feed: RSSFeed)

    @Delete
    suspend fun deleteFeed(feed: RSSFeed)

    @Query("SELECT * FROM rss_posts WHERE feedId = :feedId ORDER BY pubDate DESC")
    fun getPostsByFeedId(feedId: Long): Flow<List<RSSPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<RSSPost>)

    @Query("DELETE FROM rss_posts WHERE feedId = :feedId")
    suspend fun deletePostsByFeedId(feedId: Long)
}
