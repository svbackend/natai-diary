package com.svbackend.natai.android

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.net.ConnectivityManager
import androidx.room.Room
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.storage.CredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.repository.DiaryRepository
import com.svbackend.natai.android.service.ApiSyncService

class AppContainer(context: Context) {
    private val db = Room
        .databaseBuilder(context, DiaryDatabase::class.java, "DIARY")
        .build()

    val auth0 = Auth0(
        "3VokevDUxFNDqNQYLPb0kviShzoXvjyL",
        "natai.eu.auth0.com"
    )

    val auth0ApiClient = AuthenticationAPIClient(auth0)
    val sharedPrefs = SharedPreferencesStorage(context, context.getString(R.string.preference_file_key))
    val credentialsManager = CredentialsManager(auth0ApiClient, sharedPrefs)

    private val getApiToken = {
        val prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE)
        prefs.getString("id_token", null)
    }
    private val apiClient = ApiClient(getApiToken)

    val diaryRepository = DiaryRepository(db, apiClient)

    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val apiSyncService = ApiSyncService(
        apiClient, diaryRepository
    )
}