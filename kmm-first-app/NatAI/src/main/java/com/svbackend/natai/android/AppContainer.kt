package com.svbackend.natai.android

import android.content.Context
import androidx.room.Room
import com.auth0.android.Auth0
import com.svbackend.natai.android.repository.DiaryDbRepository

class AppContainer(context: Context) {
    private val db = Room
        .databaseBuilder(context, DiaryDatabase::class.java, "DIARY")
        .build()

    val auth0 = Auth0(
        "3VokevDUxFNDqNQYLPb0kviShzoXvjyL",
        "natai.eu.auth0.com"
    )

    val diaryRepository = DiaryDbRepository(db)
}