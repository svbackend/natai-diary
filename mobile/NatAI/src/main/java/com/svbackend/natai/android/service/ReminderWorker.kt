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
import java.util.*

const val hour = 9
const val minute = 30

class ReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val di = AppContainer.getInstance(applicationContext)

        val currentDate = Calendar.getInstance()

        if (itsLate(currentDate)) {
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
        firstNotificationDate.set(Calendar.AM_PM, Calendar.PM)
        firstNotificationDate.set(Calendar.HOUR, hour)
        firstNotificationDate.set(Calendar.MINUTE, minute)

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC, firstNotificationDate.timeInMillis, pendingIntent
        )

        Result.success()
    }

    private fun itsLate(currentDate: Calendar): Boolean {
        if (currentDate.get(Calendar.AM_PM) != Calendar.PM) {
            return false
        }

        if (currentDate.get(Calendar.HOUR) < hour) {
            return false
        }

        if (currentDate.get(Calendar.HOUR) == hour && currentDate.get(Calendar.MINUTE) < minute) {
            return false
        }

        return true
    }
}