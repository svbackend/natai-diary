package com.svbackend.natai.android.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.query.UserQuery
import com.svbackend.natai.android.repository.UserRepository

class ManageAccountViewModel(application: Application) : AndroidViewModel(application) {
    val repository: UserRepository = (application as DiaryApplication).appContainer.userRepository
    val prefs: SharedPreferences = (application as DiaryApplication).appContainer.sharedPrefs
    val apiClient: ApiClient = (application as DiaryApplication).appContainer.apiClient

    val query = UserQuery(apiClient)
}