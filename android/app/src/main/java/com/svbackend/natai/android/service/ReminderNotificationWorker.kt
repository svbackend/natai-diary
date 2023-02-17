package com.svbackend.natai.android.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.svbackend.natai.android.AppContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// it's used for notification on android 12+, for lower versions see ReminderService
class ReminderNotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val di = AppContainer.getInstance(applicationContext)

        val manager = di.notificationManager
        val reminder = ReminderLogicService(manager)
        reminder.sendNotification(applicationContext)

        Result.success()
    }
}