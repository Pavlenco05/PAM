package com.dailyplanner.organizer

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import java.io.Serializable
import java.util.Date

@Root(name = "events")
data class EventList(
    @field:ElementList(name = "event", inline = true)
    var events: MutableList<Event> = mutableListOf()
) : Serializable {
    
    fun addEvent(event: Event) {
        events.add(event)
    }
    
    fun removeEvent(eventId: String) {
        events.removeAll { it.id == eventId }
    }
    
    fun updateEvent(updatedEvent: Event) {
        val index = events.indexOfFirst { it.id == updatedEvent.id }
        if (index != -1) {
            events[index] = updatedEvent
        }
    }
    
    fun getEventById(eventId: String): Event? {
        return events.find { it.id == eventId }
    }
    
    fun searchEvents(query: String): List<Event> {
        if (query.isBlank()) return events
        val lowercaseQuery = query.lowercase()
        return events.filter { 
            it.title.lowercase().contains(lowercaseQuery) || 
            it.description.lowercase().contains(lowercaseQuery) 
        }
    }
    
    fun getEventsForDate(date: String): List<Event> {
        return events.filter { it.date == date }
    }
    
    fun getTodayEvents(): List<Event> {
        return events.filter { it.isToday() }
    }
    
    fun getTomorrowEvents(): List<Event> {
        return events.filter { it.isTomorrow() }
    }
    
    fun getThisWeekEvents(): List<Event> {
        return events.filter { it.isThisWeek() }
    }
    
    fun getUpcomingEvents(): List<Event> {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(Date())
        return events.filter { it.date >= today }.sortedBy { it.getDateTimeAsDate() }
    }
}