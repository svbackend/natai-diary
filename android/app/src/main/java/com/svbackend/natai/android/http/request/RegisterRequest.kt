package com.svbackend.natai.android.http.request

data class RegisterRequest(
    val email: String,
    val name: String,
    val password: String
)

