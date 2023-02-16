package com.svbackend.natai.android.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime

class ReminderViewModel(application: Application) : AndroidViewModel(application) {
    val isReminderEnabledState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val app: DiaryApplication = (application as DiaryApplication)
    private val prefs: SharedPreferences = app.appContainer.sharedPrefs

    fun changeReminderTime(newTime: LocalTime) {
        viewModelScope.launch {
            prefs.edit()
                .putString(app.getString(R.string.pref_reminder_time_key), newTime.toString())
                .apply()
        }
    }

    fun initIsReminderEnabled() {
        val isEnabled = prefs.getBoolean(app.getString(R.string.pref_reminder_enabled_key), false)
        isReminderEnabledState.value = isEnabled
    }

    fun toggleReminder(isEnabled: Boolean) {
        viewModelScope.launch {
            isReminderEnabledState.emit(isEnabled)
            prefs.edit()
                .putBoolean(app.getString(R.string.pref_reminder_enabled_key), isEnabled)
                .apply()
        }

    }
}