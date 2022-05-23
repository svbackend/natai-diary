package com.svbackend.natai.android.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.svbackend.natai.android.AppContainer
import com.svbackend.natai.android.model.REMINDER_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class ReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val di = AppContainer.getInstance(applicationContext)

        val alarmManager = di.alarmManager
        val reminderReceiverIntent = Intent(applicationContext, AlarmReceiver::class.java)

        reminderReceiverIntent.putExtra("reminderId", REMINDER_ID)
        val pendingIntent =
            PendingIntent.getBroadcast(
                applicationContext,
                REMINDER_ID.toInt(),
                reminderReceiverIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

        val firstNotificationDate: Calendar = GregorianCalendar.getInstance()
        firstNotificationDate.set(Calendar.AM_PM, Calendar.PM)
        firstNotificationDate.set(Calendar.HOUR, 9)
        firstNotificationDate.set(Calendar.MINUTE, 30)

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC, firstNotificationDate.timeInMillis, pendingIntent
        )

        Result.success()
    }
}