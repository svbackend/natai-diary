package com.svbackend.natai.android.http.exception

class RegistrationErrorException() : Exception() {
    override val message: String
        get() = "Error! Check your data and try again"
}