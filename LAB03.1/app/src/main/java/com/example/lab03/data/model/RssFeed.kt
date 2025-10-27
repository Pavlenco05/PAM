package com.example.lab03.data.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

data class FeedEntity(
    val url: String,
    val title: String,
    val description: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Root(name = "rss", strict = false)
data class RssFeed @JvmOverloads constructor(
    @field:Element(name = "channel")
    var channel: Channel = Channel()
)


@Root(name = "channel", strict = false)
data class Channel @JvmOverloads constructor(
    @field:Element(name = "title", required = false)
    var title: String = "",

    @field:Element(name = "link", required = false)
    var link: String = "",

    @field:Element(name = "description", required = false)
    var description: String = "",

    @field:Element(name = "language", required = false)
    var language: String = "",

    @field:ElementList(entry = "item", inline = true, required = false)
    var items: List<Item> = mutableListOf()
)

@Root(name = "item", strict = false)
data class Item @JvmOverloads constructor(
    @field:Element(name = "title", required = false)
    var title: String = "",

    @field:Element(name = "link", required = false)
    var link: String = "",

    @field:Element(name = "description", required = false)
    var description: String = "",

    @field:Element(name = "pubDate", required = false)
    var pubDate: String = "",

    @field:Element(name = "guid", required = false)
    var guid: String = ""
)

data class FeedItemEntity(
    val guid: String,
    val feedUrl: String,
    val title: String,
    val link: String,
    val description: String,
    val pubDate: String,
    val isRead: Boolean = false,
    val isSaved: Boolean = false
)
