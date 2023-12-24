package com.svbackend.natai.android.http.request

data class UpdateProfileRequest(
    val name: String,
    val cityId: Int,
    val timezoneOffset: Int = 0,
    val enableEmailNotifications: Boolean = true,
)
