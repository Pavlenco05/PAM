package com.example.lab03.repository

import com.example.lab03.data.RSSDao
import com.example.lab03.data.RSSFeed
import com.example.lab03.data.RSSPost
import com.example.lab03.service.RSSService
import kotlinx.coroutines.flow.Flow

class RSSRepository(
    private val rssDao: RSSDao,
    private val rssService: RSSService
) {
    fun getAllFeeds(): Flow<List<RSSFeed>> = rssDao.getAllFeeds()
    
    suspend fun getFeedById(feedId: Long) = rssDao.getFeedById(feedId)
    
    fun getPostsByFeedId(feedId: Long): Flow<List<RSSPost>> = rssDao.getPostsByFeedId(feedId)
    
    suspend fun addFeed(url: String): RSSFeed {
        val feed = rssService.fetchRSSFeed(url)
        val feedId = rssDao.insertFeed(feed)
        
        // Fetch and save posts
        val posts = rssService.fetchRSSPosts(url, feed.id)
        rssDao.insertPosts(posts)
        
        return feed
    }
    
    suspend fun refreshFeed(feed: RSSFeed) {
        val posts = rssService.fetchRSSPosts(feed.url, feed.id)
        rssDao.deletePostsByFeedId(feed.id)
        rssDao.insertPosts(posts)
    }
    
    suspend fun deleteFeed(feed: RSSFeed) {
        rssDao.deletePostsByFeedId(feed.id)
        rssDao.deleteFeed(feed)
    }
}
