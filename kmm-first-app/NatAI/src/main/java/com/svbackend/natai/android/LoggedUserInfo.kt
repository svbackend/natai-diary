package com.svbackend.natai.android

data class LoggedUserInfo(
    val name: String,
    val email: String,
    val pictureUrl: String,
) {
    fun hasName(): Boolean = name != email
}
