package com.svbackend.natai.android.service

import android.content.SharedPreferences
import java.time.LocalTime

const val REMINDER_TIME_KEY = "com.svbackend.natai.REMINDER_TIME"
const val REMINDER_ENABLED_KEY = "com.svbackend.natai.REMINDER_ENABLED"

class ReminderDataStore(
    private val prefs: SharedPreferences
) {
    fun getReminderTime(): LocalTime {
        val time = prefs.getString(REMINDER_TIME_KEY, "21:30")
        val timeParts = time?.split(":")
        val hour = timeParts?.get(0)?.toInt() ?: 21
        val minute = timeParts?.get(1)?.toInt() ?: 30
        return LocalTime.of(hour, minute)
    }

    fun isReminderEnabled(): Boolean {
        return prefs.getBoolean(REMINDER_ENABLED_KEY, false)
    }

    fun setReminderTime(newTime: LocalTime) {
        prefs.edit()
            .putString(REMINDER_TIME_KEY, newTime.toString())
            .apply()
    }

    fun setReminderEnabled(isEnabled: Boolean) {
        prefs.edit()
            .putBoolean(REMINDER_ENABLED_KEY, isEnabled)
            .apply()
    }
}