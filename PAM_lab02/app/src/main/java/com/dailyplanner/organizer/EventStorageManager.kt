package com.dailyplanner.organizer

import android.content.Context
import org.simpleframework.xml.core.Persister
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class EventStorageManager(private val context: Context) {
    
    private val fileName = "events.xml"
    private val serializer = Persister()
    
    private fun getEventsFile(): File {
        return File(context.filesDir, fileName)
    }
    
    fun saveEvents(eventList: EventList): Boolean {
        return try {
            val file = getEventsFile()
            val outputStream = FileOutputStream(file)
            serializer.write(eventList, outputStream)
            outputStream.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun loadEvents(): EventList {
        return try {
            val file = getEventsFile()
            if (!file.exists()) {
                return EventList()
            }
            
            val inputStream = FileInputStream(file)
            val eventList = serializer.read(EventList::class.java, inputStream)
            inputStream.close()
            eventList
        } catch (e: Exception) {
            e.printStackTrace()
            EventList()
        }
    }
    
    fun addEvent(event: Event): Boolean {
        val eventList = loadEvents()
        eventList.addEvent(event)
        return saveEvents(eventList)
    }
    
    fun updateEvent(event: Event): Boolean {
        val eventList = loadEvents()
        eventList.updateEvent(event)
        return saveEvents(eventList)
    }
    
    fun deleteEvent(eventId: String): Boolean {
        val eventList = loadEvents()
        eventList.removeEvent(eventId)
        return saveEvents(eventList)
    }
    
    fun getEventById(eventId: String): Event? {
        val eventList = loadEvents()
        return eventList.getEventById(eventId)
    }
    
    fun getAllEvents(): List<Event> {
        val eventList = loadEvents()
        return eventList.events
    }
    
    fun searchEvents(query: String): List<Event> {
        val eventList = loadEvents()
        return eventList.searchEvents(query)
    }
    
    fun getEventsForDate(date: String): List<Event> {
        val eventList = loadEvents()
        return eventList.getEventsForDate(date)
    }
    
    fun getTodayEvents(): List<Event> {
        val eventList = loadEvents()
        return eventList.getTodayEvents()
    }
    
    fun getTomorrowEvents(): List<Event> {
        val eventList = loadEvents()
        return eventList.getTomorrowEvents()
    }
    
    fun getThisWeekEvents(): List<Event> {
        val eventList = loadEvents()
        return eventList.getThisWeekEvents()
    }
    
    fun getUpcomingEvents(): List<Event> {
        val eventList = loadEvents()
        return eventList.getUpcomingEvents()
    }
}