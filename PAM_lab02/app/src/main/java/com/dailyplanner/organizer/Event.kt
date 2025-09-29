package com.dailyplanner.organizer

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Root(name = "event")
data class Event(
    @field:Attribute(name = "id")
    var id: String = UUID.randomUUID().toString(),
    
    @field:Element(name = "title")
    var title: String = "",
    
    @field:Element(name = "description")
    var description: String = "",
    
    @field:Element(name = "date")
    var date: String = "",
    
    @field:Element(name = "time")
    var time: String = "",
    
    @field:Element(name = "createdAt")
    var createdAt: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
    
    @field:Element(name = "isCompleted")
    var isCompleted: Boolean = false
) : Serializable {
    
    fun getFormattedDateTime(): String {
        return "$date $time"
    }
    
    fun getDateTimeAsDate(): Date? {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            format.parse("$date $time")
        } catch (e: Exception) {
            null
        }
    }
    
    fun isToday(): Boolean {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return date == today
    }
    
    fun isTomorrow(): Boolean {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val tomorrow = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        return date == tomorrow
    }
    
    fun isThisWeek(): Boolean {
        val eventDate = getDateTimeAsDate() ?: return false
        val calendar = Calendar.getInstance()
        val today = calendar.time
        
        calendar.add(Calendar.DAY_OF_WEEK, -calendar.get(Calendar.DAY_OF_WEEK) + 1) // Start of week
        val weekStart = calendar.time
        
        calendar.add(Calendar.DAY_OF_WEEK, 6) // End of week
        val weekEnd = calendar.time
        
        return eventDate >= weekStart && eventDate <= weekEnd
    }
}