package com.example.lab03.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.lab03.data.model.FeedEntity
import com.example.lab03.data.model.FeedItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class FeedDatabaseHelper(context: Context) : 
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "RssFeed.db"

        // Feed table
        private const val TABLE_FEEDS = "feeds"
        private const val COLUMN_FEED_URL = "url"
        private const val COLUMN_FEED_TITLE = "title"
        private const val COLUMN_FEED_DESCRIPTION = "description"
        private const val COLUMN_FEED_LAST_UPDATED = "last_updated"

        // Feed items table
        private const val TABLE_FEED_ITEMS = "feed_items"
        private const val COLUMN_ITEM_GUID = "guid"
        private const val COLUMN_ITEM_FEED_URL = "feed_url"
        private const val COLUMN_ITEM_TITLE = "title"
        private const val COLUMN_ITEM_LINK = "link"
        private const val COLUMN_ITEM_DESCRIPTION = "description"
        private const val COLUMN_ITEM_PUB_DATE = "pub_date"
        private const val COLUMN_ITEM_IS_READ = "is_read"
        private const val COLUMN_ITEM_IS_SAVED = "is_saved"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createFeedsTable = """
            CREATE TABLE $TABLE_FEEDS (
                $COLUMN_FEED_URL TEXT PRIMARY KEY,
                $COLUMN_FEED_TITLE TEXT,
                $COLUMN_FEED_DESCRIPTION TEXT,
                $COLUMN_FEED_LAST_UPDATED INTEGER
            )
        """.trimIndent()

        val createFeedItemsTable = """
            CREATE TABLE $TABLE_FEED_ITEMS (
                $COLUMN_ITEM_GUID TEXT PRIMARY KEY,
                $COLUMN_ITEM_FEED_URL TEXT,
                $COLUMN_ITEM_TITLE TEXT,
                $COLUMN_ITEM_LINK TEXT,
                $COLUMN_ITEM_DESCRIPTION TEXT,
                $COLUMN_ITEM_PUB_DATE TEXT,
                $COLUMN_ITEM_IS_READ INTEGER DEFAULT 0,
                $COLUMN_ITEM_IS_SAVED INTEGER DEFAULT 0,
                FOREIGN KEY($COLUMN_ITEM_FEED_URL) REFERENCES $TABLE_FEEDS($COLUMN_FEED_URL) ON DELETE CASCADE
            )
        """.trimIndent()

        db.execSQL(createFeedsTable)
        db.execSQL(createFeedItemsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FEED_ITEMS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FEEDS")
        onCreate(db)
    }

    // Feed operations
    fun insertFeed(feed: FeedEntity) {
        val db = writableDatabase
        try {
            val values = ContentValues().apply {
                put(COLUMN_FEED_URL, feed.url)
                put(COLUMN_FEED_TITLE, feed.title)
                put(COLUMN_FEED_DESCRIPTION, feed.description)
                put(COLUMN_FEED_LAST_UPDATED, feed.lastUpdated)
            }
            db.insertWithOnConflict(TABLE_FEEDS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        } finally {
            db.close()
        }
    }

    fun getAllFeeds(): Flow<List<FeedEntity>> = flow {
        val feedList = mutableListOf<FeedEntity>()
        val db = readableDatabase
        try {
            val cursor = db.query(
                TABLE_FEEDS,
                null,
                null,
                null,
                null,
                null,
                null
            )

            try {
                while (cursor.moveToNext()) {
                    val url = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEED_URL))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEED_TITLE))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEED_DESCRIPTION))
                    val lastUpdated = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FEED_LAST_UPDATED))

                    feedList.add(
                        FeedEntity(
                            url = url,
                            title = title,
                            description = description,
                            lastUpdated = lastUpdated
                        )
                    )
                }
            } finally {
                cursor.close()
            }
        } finally {
            db.close()
        }
        emit(feedList)
    }.flowOn(Dispatchers.IO)

    fun getFeedByUrl(url: String): FeedEntity? {
        val db = readableDatabase
        var feed: FeedEntity? = null

        try {
            val cursor = db.query(
                TABLE_FEEDS,
                null,
                "$COLUMN_FEED_URL = ?",
                arrayOf(url),
                null,
                null,
                null
            )

            try {
                if (cursor.moveToFirst()) {
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEED_TITLE))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEED_DESCRIPTION))
                    val lastUpdated = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FEED_LAST_UPDATED))

                    feed = FeedEntity(
                        url = url,
                        title = title,
                        description = description,
                        lastUpdated = lastUpdated
                    )
                }
            } finally {
                cursor.close()
            }
        } finally {
            db.close()
        }
        return feed
    }

    fun deleteFeed(url: String) {
        val db = writableDatabase
        try {
            db.delete(TABLE_FEEDS, "$COLUMN_FEED_URL = ?", arrayOf(url))
        } finally {
            db.close()
        }
    }

    // Feed item operations
    fun insertFeedItem(item: FeedItemEntity) {
        val db = writableDatabase
        try {
            val values = ContentValues().apply {
                put(COLUMN_ITEM_GUID, item.guid)
                put(COLUMN_ITEM_FEED_URL, item.feedUrl)
                put(COLUMN_ITEM_TITLE, item.title)
                put(COLUMN_ITEM_LINK, item.link)
                put(COLUMN_ITEM_DESCRIPTION, item.description)
                put(COLUMN_ITEM_PUB_DATE, item.pubDate)
                put(COLUMN_ITEM_IS_READ, if (item.isRead) 1 else 0)
                put(COLUMN_ITEM_IS_SAVED, if (item.isSaved) 1 else 0)
            }
            db.insertWithOnConflict(TABLE_FEED_ITEMS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        } finally {
            db.close()
        }
    }

    fun insertFeedItems(items: List<FeedItemEntity>) {
        val db = writableDatabase
        try {
            db.beginTransaction()
            try {
                for (item in items) {
                    val values = ContentValues().apply {
                        put(COLUMN_ITEM_GUID, item.guid)
                        put(COLUMN_ITEM_FEED_URL, item.feedUrl)
                        put(COLUMN_ITEM_TITLE, item.title)
                        put(COLUMN_ITEM_LINK, item.link)
                        put(COLUMN_ITEM_DESCRIPTION, item.description)
                        put(COLUMN_ITEM_PUB_DATE, item.pubDate)
                        put(COLUMN_ITEM_IS_READ, if (item.isRead) 1 else 0)
                        put(COLUMN_ITEM_IS_SAVED, if (item.isSaved) 1 else 0)
                    }
                    db.insertWithOnConflict(TABLE_FEED_ITEMS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        } finally {
            db.close()
        }
    }

    fun getItemsByFeedUrl(feedUrl: String): Flow<List<FeedItemEntity>> = flow {
        val itemList = mutableListOf<FeedItemEntity>()
        val db = readableDatabase
        try {
            val cursor = db.query(
                TABLE_FEED_ITEMS,
                null,
                "$COLUMN_ITEM_FEED_URL = ?",
                arrayOf(feedUrl),
                null,
                null,
                "$COLUMN_ITEM_PUB_DATE DESC"
            )

            try {
                while (cursor.moveToNext()) {
                    val guid = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_GUID))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_TITLE))
                    val link = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_LINK))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_DESCRIPTION))
                    val pubDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_PUB_DATE))
                    val isRead = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_IS_READ)) == 1
                    val isSaved = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_IS_SAVED)) == 1

                    itemList.add(
                        FeedItemEntity(
                            guid = guid,
                            feedUrl = feedUrl,
                            title = title,
                            link = link,
                            description = description,
                            pubDate = pubDate,
                            isRead = isRead,
                            isSaved = isSaved
                        )
                    )
                }
            } finally {
                cursor.close()
            }
        } finally {
            db.close()
        }
        emit(itemList)
    }.flowOn(Dispatchers.IO)

    fun getSavedItems(): Flow<List<FeedItemEntity>> = flow {
        val itemList = mutableListOf<FeedItemEntity>()
        val db = readableDatabase
        try {
            val cursor = db.query(
                TABLE_FEED_ITEMS,
                null,
                "$COLUMN_ITEM_IS_SAVED = ?",
                arrayOf("1"),
                null,
                null,
                "$COLUMN_ITEM_PUB_DATE DESC"
            )

            try {
                while (cursor.moveToNext()) {
                    val guid = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_GUID))
                    val feedUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_FEED_URL))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_TITLE))
                    val link = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_LINK))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_DESCRIPTION))
                    val pubDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_PUB_DATE))
                    val isRead = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_IS_READ)) == 1

                    itemList.add(
                        FeedItemEntity(
                            guid = guid,
                            feedUrl = feedUrl,
                            title = title,
                            link = link,
                            description = description,
                            pubDate = pubDate,
                            isRead = isRead,
                            isSaved = true
                        )
                    )
                }
            } finally {
                cursor.close()
            }
        } finally {
            db.close()
        }
        emit(itemList)
    }.flowOn(Dispatchers.IO)

    fun updateFeedItem(item: FeedItemEntity) {
        val db = writableDatabase
        try {
            val values = ContentValues().apply {
                put(COLUMN_ITEM_TITLE, item.title)
                put(COLUMN_ITEM_LINK, item.link)
                put(COLUMN_ITEM_DESCRIPTION, item.description)
                put(COLUMN_ITEM_PUB_DATE, item.pubDate)
                put(COLUMN_ITEM_IS_READ, if (item.isRead) 1 else 0)
                put(COLUMN_ITEM_IS_SAVED, if (item.isSaved) 1 else 0)
            }
            db.update(TABLE_FEED_ITEMS, values, "$COLUMN_ITEM_GUID = ?", arrayOf(item.guid))
        } finally {
            db.close()
        }
    }

    fun deleteFeedItems(feedUrl: String) {
        val db = writableDatabase
        try {
            db.delete(TABLE_FEED_ITEMS, "$COLUMN_ITEM_FEED_URL = ?", arrayOf(feedUrl))
        } finally {
            db.close()
        }
    }
}
