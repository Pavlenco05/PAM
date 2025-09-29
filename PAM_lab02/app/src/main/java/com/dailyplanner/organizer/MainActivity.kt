package com.dailyplanner.organizer

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    
    private lateinit var storageManager: EventStorageManager
    private lateinit var eventsAdapter: EventsAdapter
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var searchEditText: TextInputEditText
    private lateinit var calendarView: CalendarView
    private lateinit var fabAddEvent: FloatingActionButton
    
    // Filter buttons
    private lateinit var btnToday: MaterialButton
    private lateinit var btnTomorrow: MaterialButton
    private lateinit var btnThisWeek: MaterialButton
    private lateinit var btnAllEvents: MaterialButton
    private lateinit var btnDeleteEvent: MaterialButton
    
    private var currentFilter = "all"
    private var selectedDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        setupStorageManager()
        setupRecyclerView()
        setupSearch()
        setupCalendar()
        setupButtons()
        loadEvents()
    }
    
    private fun initializeViews() {
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView)
        searchEditText = findViewById(R.id.searchEditText)
        calendarView = findViewById(R.id.calendarView)
        fabAddEvent = findViewById(R.id.fabAddEvent)
        
        btnToday = findViewById(R.id.btnToday)
        btnTomorrow = findViewById(R.id.btnTomorrow)
        btnThisWeek = findViewById(R.id.btnThisWeek)
        btnAllEvents = findViewById(R.id.btnAllEvents)
        btnDeleteEvent = findViewById(R.id.btnDeleteEvent)
    }
    
    private fun setupStorageManager() {
        storageManager = EventStorageManager(this)
    }
    
    private fun setupRecyclerView() {
        eventsAdapter = EventsAdapter(
            onEventClick = { event -> openUpdateActivity(event) },
            onDeleteClick = { event -> deleteEvent(event) }
        )
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)
        eventsRecyclerView.adapter = eventsAdapter
    }
    
    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterEvents()
            }
        })
    }
    
    private fun setupCalendar() {
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            filterEvents()
        }
    }
    
    private fun setupButtons() {
        fabAddEvent.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }
        
        btnToday.setOnClickListener {
            setActiveFilter("today")
            filterEvents()
        }
        
        btnTomorrow.setOnClickListener {
            setActiveFilter("tomorrow")
            filterEvents()
        }
        
        btnThisWeek.setOnClickListener {
            setActiveFilter("week")
            filterEvents()
        }
        
        btnAllEvents.setOnClickListener {
            setActiveFilter("all")
            filterEvents()
        }
        
        btnDeleteEvent.setOnClickListener {
            if (eventsAdapter.selectedEvent != null) {
                deleteEvent(eventsAdapter.selectedEvent!!)
            } else {
                Toast.makeText(this, "Please select an event to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun setActiveFilter(filter: String) {
        currentFilter = filter
        
        // Reset button styles
        val buttons = listOf(btnToday, btnTomorrow, btnThisWeek, btnAllEvents)
        buttons.forEach { button ->
            button.setBackgroundColor(getColor(android.R.color.transparent))
            button.setTextColor(getColor(R.color.accent_color))
        }
        
        // Set active button style
        val activeButton = when (filter) {
            "today" -> btnToday
            "tomorrow" -> btnTomorrow
            "week" -> btnThisWeek
            else -> btnAllEvents
        }
        
        activeButton.setBackgroundColor(getColor(R.color.accent_color))
        activeButton.setTextColor(getColor(android.R.color.white))
    }
    
    private fun filterEvents() {
        val searchQuery = searchEditText.text.toString()
        val events = when (currentFilter) {
            "today" -> storageManager.getTodayEvents()
            "tomorrow" -> storageManager.getTomorrowEvents()
            "week" -> storageManager.getThisWeekEvents()
            "date" -> storageManager.getEventsForDate(selectedDate)
            else -> storageManager.getAllEvents()
        }
        
        val filteredEvents = if (searchQuery.isBlank()) {
            events
        } else {
            events.filter { 
                it.title.contains(searchQuery, ignoreCase = true) || 
                it.description.contains(searchQuery, ignoreCase = true) 
            }
        }
        
        eventsAdapter.updateEvents(filteredEvents)
    }
    
    private fun loadEvents() {
        filterEvents()
    }
    
    private fun openUpdateActivity(event: Event) {
        val intent = Intent(this, UpdateActivity::class.java)
        intent.putExtra("event_id", event.id)
        startActivity(intent)
    }
    
    private fun deleteEvent(event: Event) {
        if (storageManager.deleteEvent(event.id)) {
            Toast.makeText(this, getString(R.string.event_deleted), Toast.LENGTH_SHORT).show()
            filterEvents()
        } else {
            Toast.makeText(this, "Failed to delete event", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onResume() {
        super.onResume()
        loadEvents()
    }
}