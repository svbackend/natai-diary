package com.svbackend.natai.android

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.svbackend.natai.Greeting
import com.svbackend.natai.android.model.REMINDER_ID
import com.svbackend.natai.android.service.AlarmReceiver
import com.svbackend.natai.android.service.ReminderService
import java.text.SimpleDateFormat
import java.util.*

fun greet(): String {
    return Greeting().greeting()
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv: TextView = findViewById(R.id.text_view)
        tv.text = greet()

        addReminder()
    }

    private fun addReminder() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val reminderService = ReminderService()
        val reminderReceiverIntent = Intent(this, AlarmReceiver::class.java)

        reminderReceiverIntent.putExtra("reminderId", REMINDER_ID)
        //reminderReceiverIntent.putExtra("isServiceRunning", isServiceRunning(reminderService))
        val pendingIntent =
            PendingIntent.getBroadcast(this, REMINDER_ID.toInt(), reminderReceiverIntent, 0)

        val date: Calendar = Calendar.getInstance()
        println("Current Date and TIme : " + date.getTime())
        val timeInSecs: Long = date.getTimeInMillis()
        val afterAdding1Min = Date(timeInSecs + 10 * 1000)
        println("After adding 1 min : $afterAdding1Min")

        val formattedDate = formatDate(afterAdding1Min.time, "dd/MM/YYYY HH:mm")

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, afterAdding1Min.time, pendingIntent
        )

        Toast.makeText(this, "Alarm is set at : $formattedDate", Toast.LENGTH_SHORT).show()
        //finish()
    }

    private fun formatDate(timeInMillis: Long, format: String): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(timeInMillis)
    }

    @Suppress("DEPRECATION")
    private fun isServiceRunning(reminderService: ReminderService): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (reminderService.javaClass.name == service.service.className) {
                println("isMyServiceRunning? TRUE")
                return true
            }
        }
        println("isMyServiceRunning? FALSE")
        return false
    }
}
