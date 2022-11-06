package com.svbackend.natai.android.repository

import com.svbackend.natai.android.DiaryDatabase
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.entity.Tag
import com.svbackend.natai.android.entity.relation.NoteWithTags
import com.svbackend.natai.android.http.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant

class UserRepository(
    private val db: DiaryDatabase,
    private val api: ApiClient
) {
    fun login(email: String, password: String) {
        val response = api.login(email, password)
    }
}