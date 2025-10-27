package com.example.lab03

import android.app.Application
import com.example.lab03.data.local.FeedDatabaseHelper
import com.example.lab03.data.remote.RetrofitClient
import com.example.lab03.data.repository.FeedRepository

class RssReaderApp : Application() {
    val dbHelper by lazy { FeedDatabaseHelper(this) }
    val repository by lazy {
        FeedRepository(
            rssService = RetrofitClient.createRssService(),
            dbHelper = dbHelper
        )
    }
}
