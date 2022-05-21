package com.svbackend.natai.android.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.repository.DiaryRepository
import kotlinx.coroutines.launch

class SplashViewModel(application: Application) : AndroidViewModel(application) {
    val repository: DiaryRepository = (application as DiaryApplication).appContainer.diaryRepository

    private val _isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

//    private val _startDestination: MutableState<String> = mutableStateOf(Route.MainRoute.withArgs())
//    val startDestination: State<String> = _startDestination

    init {
        viewModelScope.launch {
            repository.notes.collect { }
            _isLoading.value = false
        }
    }

}