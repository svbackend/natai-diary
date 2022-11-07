package com.svbackend.natai.android.repository

import com.svbackend.natai.android.DiaryDatabase
import com.svbackend.natai.android.entity.User
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.http.request.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(
    private val db: DiaryDatabase,
    private val api: ApiClient
) {
    suspend fun getUserByCloudId(cloudId: String): User? = withContext(Dispatchers.IO) {
        db
            .userDAO()
            .getUserByCloudId(cloudId)
    }

    suspend fun login(email: String, password: String): User {
        val response = api.login(
            LoginRequest(
                email,
                password
            )
        )

        val userId = response.user.id
        val newApiToken = response.apiToken

        val existingUser = db.userDAO().getUserByCloudId(userId.toString())

        if (existingUser == null) {
            val cloudUser = response.user
            val newLocalUser = User(
                cloudId = userId.toString(),
                email = email,
                apiToken = newApiToken,
                name = cloudUser.name,
            )

            db.userDAO().insertUser(newLocalUser)

            return newLocalUser
        } else {

            val updatedUser = existingUser.copy(
                apiToken = newApiToken
            )

            db.userDAO().updateUser(updatedUser)

            return updatedUser
        }
    }
}