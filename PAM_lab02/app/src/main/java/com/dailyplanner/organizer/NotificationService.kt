package com.dailyplanner.organizer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import java.util.*

class NotificationService : Service() {
    
    private lateinit var storageManager: EventStorageManager
    private lateinit var notificationManager: NotificationManager
    private lateinit var vibrator: Vibrator
    
    companion object {
        private const val CHANNEL_ID = "daily_planner_channel"
        private const val NOTIFICATION_ID = 1
        private const val REQUEST_CODE = 100
    }
    
    override fun onCreate() {
        super.onCreate()
        storageManager = EventStorageManager(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        checkUpcomingEvents()
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Daily Planner Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for upcoming events"
                enableVibration(true)
                enableLights(true)
            }
            
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun checkUpcomingEvents() {
        val upcomingEvents = storageManager.getUpcomingEvents()
        val now = Date()
        
        upcomingEvents.forEach { event ->
            val eventDate = event.getDateTimeAsDate()
            if (eventDate != null) {
                val timeDiff = eventDate.time - now.time
                val minutesUntilEvent = timeDiff / (1000 * 60)
                
                // Notify 15 minutes before event
                if (minutesUntilEvent in 1..15) {
                    showNotification(event)
                    vibrateDevice()
                }
            }
        }
    }
    
    private fun showNotification(event: Event) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText("${event.title} - ${event.getFormattedDateTime()}")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("${event.title}\n${event.description}\nTime: ${event.getFormattedDateTime()}"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .build()
        
        notificationManager.notify(NOTIFICATION_ID + event.id.hashCode(), notification)
    }
    
    private fun vibrateDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createWaveform(
                longArrayOf(0, 500, 200, 500),
                -1
            )
            vibrator.vibrate(vibrationEffect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 500, 200, 500), -1)
        }
    }
}