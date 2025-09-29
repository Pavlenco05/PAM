package com.dailyplanner.organizer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class EventsAdapter(
    private val onEventClick: (Event) -> Unit,
    private val onDeleteClick: (Event) -> Unit
) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {
    
    private var events: List<Event> = emptyList()
    var selectedEvent: Event? = null
        private set
    
    fun updateEvents(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
    }
    
    override fun getItemCount(): Int = events.size
    
    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventTitle: TextView = itemView.findViewById(R.id.eventTitle)
        private val eventDescription: TextView = itemView.findViewById(R.id.eventDescription)
        private val eventDate: TextView = itemView.findViewById(R.id.eventDate)
        private val eventTime: TextView = itemView.findViewById(R.id.eventTime)
        private val btnUpdateEvent: MaterialButton = itemView.findViewById(R.id.btnUpdateEvent)
        
        fun bind(event: Event) {
            eventTitle.text = event.title
            eventDescription.text = event.description
            eventDate.text = formatDate(event.date)
            eventTime.text = event.time
            
            // Set click listeners
            itemView.setOnClickListener {
                selectedEvent = event
                onEventClick(event)
            }
            
            btnUpdateEvent.setOnClickListener {
                selectedEvent = event
                onEventClick(event)
            }
            
            // Highlight selected event
            if (selectedEvent?.id == event.id) {
                itemView.setBackgroundColor(itemView.context.getColor(R.color.light_gray))
            } else {
                itemView.setBackgroundColor(itemView.context.getColor(android.R.color.transparent))
            }
        }
        
        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                dateString
            }
        }
    }
}