package com.svbackend.natai.android.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svbackend.natai.android.DiaryApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime

class ReminderViewModel(application: Application) : AndroidViewModel(application) {
    val isReminderEnabledState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val app: DiaryApplication = (application as DiaryApplication)
    private val reminderDataStore = app.appContainer.reminderDataStore

    val isSaving: MutableStateFlow<Boolean> = MutableStateFlow(false)


    suspend fun changeReminderTime(newTime: LocalTime) {
        isSaving.emit(true)
        reminderDataStore.setReminderTime(newTime)
        isSaving.emit(false)
    }

    fun initReminderTime(): LocalTime {
        return reminderDataStore.getReminderTime()
    }

    fun initIsReminderEnabled() {
        val isEnabled = reminderDataStore.isReminderEnabled()
        isReminderEnabledState.value = isEnabled
    }

    fun toggleReminder(isEnabled: Boolean) {
        viewModelScope.launch {
            isReminderEnabledState.emit(isEnabled)
            reminderDataStore.setReminderEnabled(isEnabled)
        }

    }
}