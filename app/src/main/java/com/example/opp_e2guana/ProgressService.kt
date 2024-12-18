package com.example.opp_e2guana

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.app.Service
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.GlobalScope

const val PROG_MAX = 100
const val SERVICE_ID = 1

class ProgressService: Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    /*    val contentIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = pendingIntent.getActivity(this, 0, contentIntent, PendingIntent.FLAG_MUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, App.PRGRESS_CHANNEL_ID)
            .setContentTitle("Service Test")
            .setContentText("Service is in progress")
            .setSmallIcon(R.drawable.ic_search_icon)
            .setContentIntent(pendingIntent)
            .setProgress(PROG_MAX, 0, false)

        startForeground(SERVICE_ID, notificationBuilder.build())

        GlobalScope.launch {
            val notiManager = getSystemService(NotificationManager::class.java)

            for(progress in 1 .. PROG_MAX){
                delay(200)
                notificationBuilder.setProgress(PROG_MAX, progress, false)
                notiManager.notify(SERVICE_ID, notificationBuilder.build())
                Log.d("pregress service", "progress = $progress")
            }
            stopForeground(true)
            stopSelf()
        }

        return START_NOT_STICKY*/

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}