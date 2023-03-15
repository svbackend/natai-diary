package com.svbackend.natai.android.utils

import android.content.SharedPreferences

fun SharedPreferences.isGuest(): Boolean {
    val cloudId = getUserCloudId()
    return cloudId == null || cloudId.isEmpty()
}

fun SharedPreferences.isLoggedIn(): Boolean = !isGuest()

fun SharedPreferences.getUserCloudId(): String? = getString("cloud_id", null)