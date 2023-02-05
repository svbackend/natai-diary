package com.svbackend.natai.android.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.svbackend.natai.android.MainActivity
import com.svbackend.natai.android.R
import com.svbackend.natai.android.model.Reminder


// used for sending reminder notifications for Android API less than 31
class ReminderService : Service() {

    override fun onCreate() {
        super.onCreate()
        // enable to play a custom ring tone
        /*mediaPlayer = MediaPlayer.create(this, R.raw.alarm_ringtone)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()*/
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val manager = getSystemService(NotificationManager::class.java)

        if (manager is NotificationManager) {
            val reminder = ReminderLogicService(manager)
            reminder.sendNotification(applicationContext)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}