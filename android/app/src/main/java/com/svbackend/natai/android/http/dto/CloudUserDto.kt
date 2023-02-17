package com.svbackend.natai.android.http.dto

data class CloudUserDto(
    val id: java.util.UUID, // cloud id
    val email: String,
    val isEmailVerified: Boolean,
    val name: String,
    val roles: List<String>
)