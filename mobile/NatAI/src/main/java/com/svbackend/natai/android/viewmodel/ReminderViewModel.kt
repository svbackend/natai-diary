package com.svbackend.natai.android.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.R
import com.svbackend.natai.android.ui.UserTheme
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

    fun getCurrentTheme(): UserTheme {
        return UserTheme.strToTheme(
            prefs.getString(app.getString(R.string.pref_theme_key), null) ?: "Default"
        )
    }

    fun isReminderEnabled(): Boolean {
        return prefs.getBoolean(app.getString(R.string.pref_reminder_enabled_key), false)
    }
}