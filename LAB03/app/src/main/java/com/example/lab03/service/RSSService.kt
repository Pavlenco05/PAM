package com.example.lab03.service

import com.example.lab03.data.RSSFeed
import com.example.lab03.data.RSSPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream

class RSSService {
    private val client = OkHttpClient()
    private val parser = RSSParser()

    suspend fun fetchRSSFeed(url: String): RSSFeed = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("HTTP error: ${response.code}")
            }
            
            response.body?.byteStream()?.use { inputStream ->
                parser.parseRSSFeed(inputStream, url)
            } ?: throw Exception("Empty response body")
        }
    }

    suspend fun fetchRSSPosts(url: String, feedId: Long): List<RSSPost> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("HTTP error: ${response.code}")
            }
            
            response.body?.byteStream()?.use { inputStream ->
                parser.parseRSSPosts(inputStream, feedId)
            } ?: throw Exception("Empty response body")
        }
    }
}
