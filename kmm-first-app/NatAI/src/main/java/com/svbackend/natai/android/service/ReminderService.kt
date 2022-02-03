package com.svbackend.natai.android.service

//import com.geeklabs.remindme.activities.MainActivity
//import com.geeklabs.remindme.database.DatabaseHandler
//import com.geeklabs.remindme.models.Reminder
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.svbackend.natai.android.R
import com.svbackend.natai.android.MainActivity
import com.svbackend.natai.android.model.Reminder
import java.util.*


class ReminderService : Service() {

    override fun onCreate() {
        super.onCreate()
        println("ReminderService onCreate called")
        // enable to play a custom ring tone
        /*mediaPlayer = MediaPlayer.create(this, R.raw.alarm_ringtone)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()*/
    }

    override fun onBind(intent: Intent?): IBinder? {
        println("ReminderService onBind called")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("ReminderService onStartCommand called")

        val reminder = Reminder(
            title = "How was your day?",
            description = "Share couple of words with me!",
        )

        showAlarmNotification(reminder)

        // https://stackoverflow.com/questions/9093271/start-sticky-and-start-not-sticky
        return START_STICKY
    }

    private fun showAlarmNotification(reminder: Reminder) {
        println("ReminderService showAlarmNotification called")

        createNotificationChannel(reminder.id.toInt())
        // build notification
        val builder = NotificationCompat.Builder(this, reminder.id.toString())
            .setSmallIcon(R.drawable.app_logo) //set icon for notification
            .setContentTitle(reminder.title) //set title of notification
            .setContentText(reminder.description)//this is notification message
            .setAutoCancel(true) // makes auto cancel of notification
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) //set priority of notification

        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        //notification message will get at NotificationView
        notificationIntent.putExtra("reminderId", reminder.id)
        notificationIntent.putExtra("from", "Notification")

        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentIntent(pendingIntent)
        val notification = builder.build()

        // Add as notification
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(reminder.id.toInt(), notification)
    }

    private fun createNotificationChannel(id: Int) {
        val serviceChannel = NotificationChannel(
            id.toString(),
            "Reminder Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(serviceChannel)
    }

    override fun onDestroy() {
        println("ReminderService onDestroy called")
        super.onDestroy()
    }
}