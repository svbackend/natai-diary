package com.svbackend.natai.android.query

import androidx.compose.runtime.mutableStateOf
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.http.dto.CloudUserDto

class UserQueryException(message: String) : Exception(message)

class UserQuery(val apiClient: ApiClient) {
    val isLoading = mutableStateOf(false)
    val data = mutableStateOf<CloudUserDto?>(null)
    val error = mutableStateOf<String?>(null)

    suspend fun getUser(): CloudUserDto? {
        isLoading.value = true
        error.value = null
        data.value = null

        try {
            val response = apiClient.getCurrentUser()
            data.value = response.user
        } catch (e: UserQueryException) {
            error.value = e.message
        } finally {
            isLoading.value = false
        }

        return data.value
    }
}