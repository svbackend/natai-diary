package com.svbackend.natai.android.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        val reminderId = intent?.getLongExtra("reminderId", 0)
        val isServiceRunning = intent?.getBooleanExtra("isServiceRunning", false) ?: false

        val reminderServiceIntent = Intent(context, ReminderService::class.java)
        reminderServiceIntent.putExtra("reminderId", reminderId)

        if (!isServiceRunning) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val request = OneTimeWorkRequest.Builder(ReminderNotificationWorker::class.java)
                    .build()
                WorkManager.getInstance(context).enqueue(request)
            } else {
                context.startForegroundService(reminderServiceIntent)
            }
        }
    }
}