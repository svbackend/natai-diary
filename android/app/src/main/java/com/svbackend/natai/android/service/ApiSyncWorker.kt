package com.svbackend.natai.android.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.svbackend.natai.android.AppContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApiSyncWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val di = AppContainer.getInstance(applicationContext)

        try {
            di.apiSyncService.syncNotes()
            Result.success()
        } catch (e: Throwable) {
            e.printStackTrace()
            Result.failure()
        }
    }
}