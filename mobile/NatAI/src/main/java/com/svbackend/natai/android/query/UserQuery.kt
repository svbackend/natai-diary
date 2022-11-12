package com.svbackend.natai.android.query

import androidx.compose.runtime.mutableStateOf
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.http.response.UserDto

class UserQueryException(message: String) : Exception(message)

class UserQuery(val apiClient: ApiClient) {
    val isLoading = mutableStateOf(false)
    val data = mutableStateOf<UserDto?>(null)
    val error = mutableStateOf<String?>(null)

    suspend fun getUser() {
        isLoading.value = true
        error.value = null
        data.value = null

        try {
            data.value = apiClient.getCurrentUser()
        } catch (e: UserQueryException) {
            error.value = e.message
        } finally {
            isLoading.value = false
        }
    }
}