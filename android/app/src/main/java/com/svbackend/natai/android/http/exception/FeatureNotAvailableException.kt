package com.svbackend.natai.android.http.exception

class FeatureNotAvailableException() : Exception() {
    override val message: String
        get() = "Feature not available"
}