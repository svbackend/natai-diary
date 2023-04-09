package com.svbackend.natai.android.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.http.ApiClient
import kotlinx.coroutines.launch

class DeleteAccountViewModel(application: Application) : AndroidViewModel(application) {
    val api: ApiClient = (application as DiaryApplication).appContainer.apiClient

    val isLoading = mutableStateOf(false)
    val isError = mutableStateOf(false)

    fun deleteAccount(onSuccess: suspend () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                api.deleteAccount()
                onSuccess()
            } catch (e: Exception) {
                isError.value = true
            } finally {
                isLoading.value = false
            }
        }
    }
}