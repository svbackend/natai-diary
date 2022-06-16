package com.svbackend.natai.android

import android.app.AlarmManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.activity.ComponentActivity
import androidx.room.Room
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.storage.CredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.repository.DiaryRepository
import com.svbackend.natai.android.service.ApiSyncService

class AppContainer(context: Context) {
    private val db = DiaryDatabase.getInstance(context)

    val alarmManager = context.getSystemService(ComponentActivity.ALARM_SERVICE) as AlarmManager

    val auth0 = Auth0(
        "3VokevDUxFNDqNQYLPb0kviShzoXvjyL",
        "natai.eu.auth0.com"
    )

    val auth0ApiClient = AuthenticationAPIClient(auth0)
    val sharedPrefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE)
    val credentialsManager = CredentialsManager(auth0ApiClient, SharedPreferencesStorage(context, context.getString(R.string.preference_file_key)))

    private val getApiToken = {
        sharedPrefs.getString("id_token", null)
    }
    private val apiClient = ApiClient(getApiToken)

    val diaryRepository = DiaryRepository(db, apiClient)

    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val apiSyncService = ApiSyncService(
        apiClient, diaryRepository
    )

    companion object {
        @Volatile private var instance: AppContainer? = null

        fun getInstance(context: Context): AppContainer {
            return instance ?: synchronized(this) {
                instance ?: AppContainer(context).also { instance = it }
            }
        }
    }
}