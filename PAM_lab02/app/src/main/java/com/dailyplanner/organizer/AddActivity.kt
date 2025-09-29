package com.dailyplanner.organizer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import java.text.SimpleDateFormat
import java.util.*

class AddActivity : AppCompatActivity() {
    
    private lateinit var storageManager: EventStorageManager
    private lateinit var editTextTitle: TextInputEditText
    private lateinit var editTextDescription: TextInputEditText
    private lateinit var btnSelectDate: MaterialButton
    private lateinit var btnSelectTime: MaterialButton
    private lateinit var textSelectedDateTime: MaterialTextView
    private lateinit var btnSaveEvent: MaterialButton
    private lateinit var btnCancel: MaterialButton
    
    private var selectedDate: String = ""
    private var selectedTime: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        
        initializeViews()
        setupStorageManager()
        setupButtons()
        setDefaultDateTime()
    }
    
    private fun initializeViews() {
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDescription = findViewById(R.id.editTextDescription)
        btnSelectDate = findViewById(R.id.btnSelectDate)
        btnSelectTime = findViewById(R.id.btnSelectTime)
        textSelectedDateTime = findViewById(R.id.textSelectedDateTime)
        btnSaveEvent = findViewById(R.id.btnSaveEvent)
        btnCancel = findViewById(R.id.btnCancel)
    }
    
    private fun setupStorageManager() {
        storageManager = EventStorageManager(this)
    }
    
    private fun setupButtons() {
        btnSelectDate.setOnClickListener {
            showDatePicker()
        }
        
        btnSelectTime.setOnClickListener {
            showTimePicker()
        }
        
        btnSaveEvent.setOnClickListener {
            saveEvent()
        }
        
        btnCancel.setOnClickListener {
            finish()
        }
    }
    
    private fun setDefaultDateTime() {
        val calendar = Calendar.getInstance()
        selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        selectedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
        updateDateTimeDisplay()
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
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
    
    private fun saveEvent() {
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
        
        val event = Event(
            title = title,
            description = description,
            date = selectedDate,
            time = selectedTime
        )
        
        if (storageManager.addEvent(event)) {
            Toast.makeText(this, getString(R.string.event_saved), Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Failed to save event", Toast.LENGTH_SHORT).show()
        }
    }
}