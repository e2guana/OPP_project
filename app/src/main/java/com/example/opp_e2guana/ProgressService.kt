package com.example.opp_e2guana

import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Intent
import android.app.Service
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ProgressService: Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int{
        val contentIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = pendingIntent.getActivity(this, 0, contentIntent, PendingIntent.FLAG_MUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, App.PRGRESS_CHANNEL_ID)
            .setContentTitle("Service Test")
            .setContentText("Service is in progress")
    }

    override fun onBind(p0: Intent?): IBinder {
        return null
    }
}