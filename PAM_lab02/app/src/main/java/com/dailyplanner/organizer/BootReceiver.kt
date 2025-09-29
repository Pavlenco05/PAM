package com.dailyplanner.organizer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Restart notification service after device boot
            val serviceIntent = Intent(context, NotificationService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}