package com.svbackend.natai.android.utils

import android.content.SharedPreferences
import java.time.Instant

const val KEY_LAST_SYNC_TIME = "last_sync_time"

fun SharedPreferences.isGuest(): Boolean {
    val cloudId = getUserCloudId()
    return cloudId == null || cloudId.isEmpty()
}

fun SharedPreferences.isLoggedIn(): Boolean = !isGuest()

fun SharedPreferences.getUserCloudId(): String? = getString("cloud_id", null)

fun SharedPreferences.getLastSyncTime(): Instant {
    val time = getLong(KEY_LAST_SYNC_TIME, 0)
    return Instant.ofEpochSecond(time)
}

fun SharedPreferences.updateLastSyncTime() {
    val time = Instant.now().epochSecond
    edit().putLong(KEY_LAST_SYNC_TIME, time).apply()
}

fun SharedPreferences.resetLastSyncTime() {
    edit().remove(KEY_LAST_SYNC_TIME).apply()
}