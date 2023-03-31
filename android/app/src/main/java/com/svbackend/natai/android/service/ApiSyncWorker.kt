package com.svbackend.natai.android.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.svbackend.natai.android.AppContainer
import com.svbackend.natai.android.utils.getLastSyncTime
import com.svbackend.natai.android.utils.updateLastSyncTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApiSyncWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val di = AppContainer.getInstance(applicationContext)
        val prefs = di.sharedPrefs
        val lastSyncTime = prefs.getLastSyncTime()

        try {
            di.apiSyncService.syncNotes(lastSyncTime)
            prefs.updateLastSyncTime()
            Result.success()
        } catch (e: Throwable) {
            e.printStackTrace()
            Result.failure()
        }
    }
}