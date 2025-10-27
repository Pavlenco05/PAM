package com.example.lab03.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "rss_feeds")
data class RSSFeed(
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val description: String,
    val url: String,
    val link: String,
    val addedDate: Date = Date()
)

@Entity(tableName = "rss_posts")
data class RSSPost(
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),
    val feedId: Long,
    val title: String,
    val description: String,
    val link: String,
    val pubDate: String
)
