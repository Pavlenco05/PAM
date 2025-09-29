package com.dailyplanner.organizer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import java.text.SimpleDateFormat
import java.util.*

class UpdateActivity : AppCompatActivity() {
    
    private lateinit var storageManager: EventStorageManager
    private lateinit var editTextTitle: TextInputEditText
    private lateinit var editTextDescription: TextInputEditText
    private lateinit var btnSelectDate: MaterialButton
    private lateinit var btnSelectTime: MaterialButton
    private lateinit var textSelectedDateTime: MaterialTextView
    private lateinit var switchCompleted: SwitchMaterial
    private lateinit var btnUpdateEvent: MaterialButton
    private lateinit var btnDeleteEvent: MaterialButton
    private lateinit var btnCancel: MaterialButton
    
    private var currentEvent: Event? = null
    private var selectedDate: String = ""
    private var selectedTime: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        
        initializeViews()
        setupStorageManager()
        loadEvent()
        setupButtons()
    }
    
    private fun initializeViews() {
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDescription = findViewById(R.id.editTextDescription)
        btnSelectDate = findViewById(R.id.btnSelectDate)
        btnSelectTime = findViewById(R.id.btnSelectTime)
        textSelectedDateTime = findViewById(R.id.textSelectedDateTime)
        switchCompleted = findViewById(R.id.switchCompleted)
        btnUpdateEvent = findViewById(R.id.btnUpdateEvent)
        btnDeleteEvent = findViewById(R.id.btnDeleteEvent)
        btnCancel = findViewById(R.id.btnCancel)
    }
    
    private fun setupStorageManager() {
        storageManager = EventStorageManager(this)
    }
    
    private fun loadEvent() {
        val eventId = intent.getStringExtra("event_id")
        if (eventId != null) {
            currentEvent = storageManager.getEventById(eventId)
            if (currentEvent != null) {
                populateFields()
            } else {
                Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "No event ID provided", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun populateFields() {
        currentEvent?.let { event ->
            editTextTitle.setText(event.title)
            editTextDescription.setText(event.description)
            selectedDate = event.date
            selectedTime = event.time
            switchCompleted.isChecked = event.isCompleted
            updateDateTimeDisplay()
        }
    }
    
    private fun setupButtons() {
        btnSelectDate.setOnClickListener {
            showDatePicker()
        }
        
        btnSelectTime.setOnClickListener {
            showTimePicker()
        }
        
        btnUpdateEvent.setOnClickListener {
            updateEvent()
        }
        
        btnDeleteEvent.setOnClickListener {
            deleteEvent()
        }
        
        btnCancel.setOnClickListener {
            finish()
        }
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(selectedDate)
            if (date != null) {
                calendar.time = date
            }
        } catch (e: Exception) {
            // Use current date if parsing fails
        }
        
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedCalendar.time)
                updateDateTimeDisplay()
            },
            year, month, day
        )
        
        datePickerDialog.show()
    }
    
    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        try {
            val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val time = inputFormat.parse(selectedTime)
            if (time != null) {
                calendar.time = time
            }
        } catch (e: Exception) {
            // Use current time if parsing fails
        }
        
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        
        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                updateDateTimeDisplay()
            },
            hour, minute, true
        )
        
        timePickerDialog.show()
    }
    
    private fun updateDateTimeDisplay() {
        if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
            val displayFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
            val dateTimeString = "$selectedDate $selectedTime"
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val date = inputFormat.parse(dateTimeString)
                textSelectedDateTime.text = displayFormat.format(date ?: Date())
            } catch (e: Exception) {
                textSelectedDateTime.text = "$selectedDate $selectedTime"
            }
        } else {
            textSelectedDateTime.text = "No date and time selected"
        }
    }
    
    private fun updateEvent() {
        val title = editTextTitle.text.toString().trim()
        val description = editTextDescription.text.toString().trim()
        
        if (title.isEmpty()) {
            editTextTitle.error = "Title is required"
            return
        }
        
        if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select date and time", Toast.LENGTH_SHORT).show()
            return
        }
        
        currentEvent?.let { event ->
            val updatedEvent = event.copy(
                title = title,
                description = description,
                date = selectedDate,
                time = selectedTime,
                isCompleted = switchCompleted.isChecked
            )
            
            if (storageManager.updateEvent(updatedEvent)) {
                Toast.makeText(this, getString(R.string.event_updated), Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to update event", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun deleteEvent() {
        currentEvent?.let { event ->
            if (storageManager.deleteEvent(event.id)) {
                Toast.makeText(this, getString(R.string.event_deleted), Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to delete event", Toast.LENGTH_SHORT).show()
            }
        }
    }
}