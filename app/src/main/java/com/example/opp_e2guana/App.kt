package com.example.opp_e2guana

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Build.VERSION_CODES

class App: Application {
    companion object {
        const val PRGRESS_CHANNEL_ID = "com.example.progressnotification"//example.e2guana.progress 이렇게 써야하는거 아닌가
    }

    override fun onCreate() {
        super.onCreate()
        if(Build.VERSION.SDK_INT >= VERSION_CODES.0) {
            getSystemService(NotificationManager::class.java).run {
                val progressChannel = NotificationChannel(
                    PRGRESS_CHANNEL_ID,
                    "Progress Test",
                    NotificationManager.IMPORTANCE_LOW
                )
                createNotificationChannel(progressChannel)

            }
        }

        //LMS 포어그라운드 서비스 10분 50초

    }
}