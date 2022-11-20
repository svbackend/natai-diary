package com.svbackend.natai.android.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.query.UserQuery
import com.svbackend.natai.android.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ManageAccountViewModel(application: Application) : AndroidViewModel(application) {
    val repository: UserRepository = (application as DiaryApplication).appContainer.userRepository
    val prefs: SharedPreferences = (application as DiaryApplication).appContainer.sharedPrefs
    val apiClient: ApiClient = (application as DiaryApplication).appContainer.apiClient

    val userQuery = UserQuery(apiClient)

    val showStillNotVerifiedError = mutableStateOf(false)

    suspend fun checkEmailVerification() {
        val user = userQuery.getUser()
        if (user != null) {
            repository.updateUser(user)

            if (!user.isEmailVerified) {
                showStillNotVerifiedError.value = true
            }
        }

        clearErrorAfterDelay()
    }

    private suspend fun clearErrorAfterDelay() {
        withContext(Dispatchers.IO) {
            Thread.sleep(5000)
            showStillNotVerifiedError.value = false
        }
    }
}