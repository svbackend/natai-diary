package com.svbackend.natai.android.http.exception

class CloudIdMissingException() : Exception() {
    override val message: String
        get() = "Error! CloudId is missing!"
}