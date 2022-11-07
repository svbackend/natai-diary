package com.svbackend.natai.android.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.entity.*
import com.svbackend.natai.android.repository.DiaryRepository
import com.svbackend.natai.android.repository.UserRepository
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    val repository: UserRepository = (application as DiaryApplication).appContainer.userRepository
    val prefs: SharedPreferences = (application as DiaryApplication).appContainer.sharedPrefs

    val isLoading = mutableStateOf(false)

    val email = mutableStateOf(
        TextFieldValue("")
    )

    val password = mutableStateOf(
        TextFieldValue("")
    )

    suspend fun login() {
        val user = repository.login(email.value.text, password.value.text)
        prefs.edit()
            .putString("api_token", user.apiToken)
            .putString("cloud_id", user.cloudId)
            .apply()
    }
}