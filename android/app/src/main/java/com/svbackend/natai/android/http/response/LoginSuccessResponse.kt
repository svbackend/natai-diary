package com.svbackend.natai.android.http.response

import com.svbackend.natai.android.http.dto.CloudUserDto

data class LoginSuccessResponse(
    val user: CloudUserDto,
    val apiToken: String
)

