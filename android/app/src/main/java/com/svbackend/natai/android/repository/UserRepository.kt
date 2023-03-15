package com.svbackend.natai.android.repository

import com.svbackend.natai.android.DiaryDatabase
import com.svbackend.natai.android.entity.User
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.http.request.LoginRequest
import com.svbackend.natai.android.http.request.RegisterRequest
import com.svbackend.natai.android.http.dto.CloudUserDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class UserRepository(
    private val db: DiaryDatabase,
    private val api: ApiClient
) {
    val users: Flow<List<User>> = db
        .userDAO()
        .getUsers()

    suspend fun getUserByCloudId(cloudId: String): Flow<User?> = withContext(Dispatchers.IO) {
        db
            .userDAO()
            .getUserByCloudId(cloudId)
    }

    suspend fun getUserByCloudIdSync(cloudId: String): User? = withContext(Dispatchers.IO) {
        db
            .userDAO()
            .getUserByCloudIdSync(cloudId)
    }

    suspend fun login(email: String, password: String): User = withContext(Dispatchers.IO) {
        val response = api.login(
            LoginRequest(
                email,
                password
            )
        )

        val cloudUser = response.user
        val userId = cloudUser.id
        val newApiToken = response.apiToken

        val existingUser = db.userDAO().getUserByCloudIdSync(userId.toString())

        if (existingUser == null) {
            val newLocalUser = User(
                cloudId = userId.toString(),
                email = cloudUser.email,
                apiToken = newApiToken,
                name = cloudUser.name,
                isEmailVerified = cloudUser.isEmailVerified
            )

            db.userDAO().insertUser(newLocalUser)
            return@withContext newLocalUser
        } else {
            val updatedUser = existingUser.copy(
                apiToken = newApiToken,
                isEmailVerified = cloudUser.isEmailVerified,
                name = cloudUser.name,
                email = cloudUser.email
            )

            db.userDAO().updateUser(updatedUser)
            return@withContext updatedUser
        }

    }

    suspend fun register(email: String, name: String, password: String): User {
        api.register(
            RegisterRequest(
                name = name,
                email = email,
                password = password
            )
        )

        return login(email, password)
    }

    suspend fun updateUserEmailVerificationStatus(user: CloudUserDto) = withContext(Dispatchers.IO) {
        val entity = getUserByCloudIdSync(user.id.toString()) ?: return@withContext

        if (entity.isEmailVerified == user.isEmailVerified) {
            return@withContext
        }

        val updatedEntity = entity.copy(
            isEmailVerified = user.isEmailVerified
        )

        db.userDAO().updateUser(updatedEntity)
    }
}