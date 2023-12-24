package com.svbackend.natai.android.http.dto

data class CloudUserProfileDto(
    val city: CityDto,
    val timezoneOffset: Int,
    val enableEmailNotifications: Boolean = true,
)