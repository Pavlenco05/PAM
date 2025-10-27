package com.example.lab03.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context

@Database(
    entities = [RSSFeed::class, RSSPost::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RSSDatabase : RoomDatabase() {
    abstract fun rssDao(): RSSDao

    companion object {
        @Volatile
        private var INSTANCE: RSSDatabase? = null

        fun getDatabase(context: Context): RSSDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RSSDatabase::class.java,
                    "rss_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
