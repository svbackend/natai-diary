package com.svbackend.natai.android

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.room.Room
import com.auth0.android.Auth0
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.repository.DiaryRepository

class AppContainer(context: Context) {
    private val db = Room
        .databaseBuilder(context, DiaryDatabase::class.java, "DIARY")
        .build()

    val auth0 = Auth0(
        "3VokevDUxFNDqNQYLPb0kviShzoXvjyL",
        "natai.eu.auth0.com"
    )

    private val getApiToken = {
        val prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE)
        prefs.getString("id_token", null)
    }
    private val apiClient = ApiClient(getApiToken)

    val diaryRepository = DiaryRepository(db, apiClient)
}