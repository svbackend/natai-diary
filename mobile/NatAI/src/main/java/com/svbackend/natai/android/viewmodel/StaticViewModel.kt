package com.svbackend.natai.android.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.http.ApiClient
import kotlinx.coroutines.launch

class StaticViewModel(application: Application) : AndroidViewModel(application) {
    val api: ApiClient = (application as DiaryApplication).appContainer.apiClient

    val terms = mutableStateOf("")

    init {
        viewModelScope.launch {
            val response = api.getStatic()
            terms.value = response.terms
        }
    }
}