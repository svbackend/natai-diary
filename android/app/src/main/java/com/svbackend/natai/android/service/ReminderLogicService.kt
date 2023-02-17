package com.svbackend.natai.android.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.svbackend.natai.android.MainActivity
import com.svbackend.natai.android.R
import com.svbackend.natai.android.model.Reminder

class ReminderLogicService(
    private val notificationManager: NotificationManager
) {

    fun sendNotification(context: Context) {
        val reminder = Reminder(
            title = "How was your day?",
            description = "Share couple of words with me!",
        )

        createNotificationChannel(reminder.id.toInt())
        // build notification
        val builder = NotificationCompat.Builder(context, reminder.id.toString())
            .setSmallIcon(R.drawable.ic_baseline_filter_vintage_24) //set icon for notification
            .setContentTitle(reminder.title) //set title of notification
            .setContentText(reminder.description)//this is notification message
            .setAutoCancel(true) // makes auto cancel of notification
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) //set priority of notification

        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        //notification message will get at NotificationView
        notificationIntent.putExtra("reminderId", reminder.id)
        notificationIntent.putExtra("from", "Notification")

        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(pendingIntent)
        val notification = builder.build()

        notificationManager.notify(reminder.id.toInt(), notification)
    }

    private fun createNotificationChannel(id: Int) {
        val serviceChannel = NotificationChannel(
            id.toString(),
            "Reminder Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(serviceChannel)
    }
}