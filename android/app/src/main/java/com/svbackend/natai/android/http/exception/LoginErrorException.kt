package com.svbackend.natai.android.http.exception

class LoginErrorException() : Exception() {
    override val message: String
        get() = "Email or password is incorrect"
}