package com.svbackend.natai.android.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.repository.DiaryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(application: Application) : AndroidViewModel(application) {
    private val _isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    fun loaded() {
        viewModelScope.launch {
            if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.R) {
                // little workaround to remove blinking (when empty state screen disappears) on Android 10
                delay(1)
            }
            _isLoading.value = false
        }
    }
}