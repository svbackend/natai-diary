package com.svbackend.natai.android.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.svbackend.natai.android.AppContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.util.*

class ReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val di = AppContainer.getInstance(applicationContext)

        val isReminderEnabled = di.reminderDataStore.isReminderEnabled()

        if (!isReminderEnabled) {
            return@withContext Result.success()
        }

        val reminderTime = di.reminderDataStore.getReminderTime()

        val currentDate = Calendar.getInstance()

        if (itsLate(currentDate, reminderTime)) {
            return@withContext Result.success()
        }

        val reminderId = buildString {
            append(currentDate.get(Calendar.YEAR))
            append(currentDate.get(Calendar.MONTH))
            append(currentDate.get(Calendar.DATE))
        }.toLong()

        val alarmManager = di.alarmManager
        val reminderReceiverIntent = Intent(applicationContext, AlarmReceiver::class.java)

        reminderReceiverIntent.putExtra("reminderId", reminderId)
        val pendingIntent =
            PendingIntent.getBroadcast(
                applicationContext,
                reminderId.toInt(),
                reminderReceiverIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

        val firstNotificationDate: Calendar = GregorianCalendar.getInstance()
        firstNotificationDate.set(Calendar.HOUR_OF_DAY, reminderTime.hour)
        firstNotificationDate.set(Calendar.MINUTE, reminderTime.minute)

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC, firstNotificationDate.timeInMillis, pendingIntent
        )

        Result.success()
    }

    private fun itsLate(currentDate: Calendar, reminderTime: LocalTime): Boolean {
        val currentHour = currentDate.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentDate.get(Calendar.MINUTE)

        if (currentHour < reminderTime.hour) {
            return false
        }

        if (currentHour == reminderTime.hour && currentMinute < reminderTime.minute) {
            return false
        }

        return true
    }
}