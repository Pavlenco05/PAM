package com.example.lab03.service

import com.example.lab03.data.RSSFeed
import com.example.lab03.data.RSSPost
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.InputStream
import java.util.Date
import javax.xml.parsers.DocumentBuilderFactory

class RSSParser {
    
    fun parseRSSFeed(inputStream: InputStream, feedUrl: String): RSSFeed {
        val document = parseXML(inputStream)
        
        val title = getTextContent(document, "title") ?: "Unknown Feed"
        val description = getTextContent(document, "description") ?: ""
        val link = getTextContent(document, "link") ?: feedUrl
        
        val items = document.getElementsByTagName("item")
        val posts = mutableListOf<RSSPost>()
        
        for (i in 0 until items.length) {
            val item = items.item(i) as Element
            val post = parseRSSPost(item, 0) // We'll set feedId later
            posts.add(post)
        }
        
        return RSSFeed(
            title = title,
            description = description,
            url = feedUrl,
            link = link,
            addedDate = Date()
        )
    }
    
    fun parseRSSPosts(inputStream: InputStream, feedId: Long): List<RSSPost> {
        val document = parseXML(inputStream)
        val items = document.getElementsByTagName("item")
        val posts = mutableListOf<RSSPost>()
        
        for (i in 0 until items.length) {
            val item = items.item(i) as Element
            val post = parseRSSPost(item, feedId)
            posts.add(post)
        }
        
        return posts
    }
    
    private fun parseRSSPost(item: Element, feedId: Long): RSSPost {
        val title = getTextContent(item, "title") ?: "No title"
        val description = getTextContent(item, "description") ?: 
                         getTextContent(item, "summary") ?: 
                         getTextContent(item, "content") ?: "No description"
        val link = getTextContent(item, "link") ?: "#"
        val pubDate = getTextContent(item, "pubDate") ?: 
                     getTextContent(item, "published") ?: "Unknown date"
        
        return RSSPost(
            feedId = feedId,
            title = title,
            description = description,
            link = link,
            pubDate = pubDate
        )
    }
    
    private fun parseXML(inputStream: InputStream): Document {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        return builder.parse(inputStream)
    }
    
    private fun getTextContent(element: Element, tagName: String): String? {
        val nodeList = element.getElementsByTagName(tagName)
        return if (nodeList.length > 0) {
            nodeList.item(0).textContent?.trim()
        } else {
            null
        }
    }
    
    private fun getTextContent(document: Document, tagName: String): String? {
        val nodeList = document.getElementsByTagName(tagName)
        return if (nodeList.length > 0) {
            nodeList.item(0).textContent?.trim()
        } else {
            null
        }
    }
}
