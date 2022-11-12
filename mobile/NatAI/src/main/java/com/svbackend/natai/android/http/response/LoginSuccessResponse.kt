package com.svbackend.natai.android.http.response

data class UserDto(
    val id: java.util.UUID,
    val email: String,
    val isEmailVerified: Boolean,
    val name: String,
    val roles: List<String>
)

data class LoginSuccessResponse(
    val user: UserDto,
    val apiToken: String
)

