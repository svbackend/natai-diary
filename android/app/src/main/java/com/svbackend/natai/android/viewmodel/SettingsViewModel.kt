package com.svbackend.natai.android.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.svbackend.natai.android.DiaryApplication
import java.time.LocalTime

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val app: DiaryApplication = (application as DiaryApplication)
    private val reminderDataStore = app.appContainer.reminderDataStore

    fun initReminderTime(): LocalTime {
        return reminderDataStore.getReminderTime()
    }

    fun initIsReminderEnabled(): Boolean {
        return reminderDataStore.isReminderEnabled()
    }
}