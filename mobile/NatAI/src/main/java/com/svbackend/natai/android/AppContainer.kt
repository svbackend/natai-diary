package com.svbackend.natai.android

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.activity.ComponentActivity
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.repository.DiaryRepository
import com.svbackend.natai.android.repository.UserRepository
import com.svbackend.natai.android.service.ApiSyncService

class AppContainer(context: Context) {
    private val db = DiaryDatabase.getInstance(context)

    val alarmManager = context.getSystemService(ComponentActivity.ALARM_SERVICE) as AlarmManager

    val notificationManager =
        context.getSystemService(NotificationManager::class.java) as NotificationManager

    val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE)

    private val getApiToken = {
        sharedPrefs.getString("api_token", null)
    }
    val apiClient = ApiClient(getApiToken)

    val diaryRepository = DiaryRepository(db, apiClient)
    val userRepository = UserRepository(db, apiClient)

    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val apiSyncService = ApiSyncService(
        apiClient, diaryRepository
    )

    companion object {
        @Volatile
        private var instance: AppContainer? = null

        fun getInstance(context: Context): AppContainer {
            return instance ?: synchronized(this) {
                instance ?: AppContainer(context).also { instance = it }
            }
        }
    }
}