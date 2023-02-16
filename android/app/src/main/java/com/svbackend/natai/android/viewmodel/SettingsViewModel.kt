package com.svbackend.natai.android.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.R
import java.time.LocalTime

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val app: DiaryApplication = (application as DiaryApplication)
    private val prefs: SharedPreferences = app.appContainer.sharedPrefs

    fun initReminderTime(): LocalTime {
        val time = prefs.getString(app.getString(R.string.pref_reminder_time_key), "21:30")
        val timeParts = time?.split(":")
        val hour = timeParts?.get(0)?.toInt() ?: 21
        val minute = timeParts?.get(1)?.toInt() ?: 30
        return LocalTime.of(hour, minute)
    }

    fun initIsReminderEnabled(): Boolean {
        return prefs.getBoolean(app.getString(R.string.pref_reminder_enabled_key), false)
    }
}