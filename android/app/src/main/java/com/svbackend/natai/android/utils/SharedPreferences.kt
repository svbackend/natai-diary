package com.svbackend.natai.android.utils

import android.content.SharedPreferences
import java.time.Instant

fun SharedPreferences.isGuest(): Boolean {
    val cloudId = getUserCloudId()
    return cloudId == null || cloudId.isEmpty()
}

fun SharedPreferences.isLoggedIn(): Boolean = !isGuest()

fun SharedPreferences.getUserCloudId(): String? = getString("cloud_id", null)

fun SharedPreferences.getLastSyncTime(): Instant {
    val time = getLong("last_sync_time", 0)
    return Instant.ofEpochSecond(time)
}

fun SharedPreferences.updateLastSyncTime() {
    val time = Instant.now().epochSecond
    edit().putLong("last_sync_time", time).apply()
}