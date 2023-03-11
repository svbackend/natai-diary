package com.svbackend.natai.android.http.exception

class DownloadAttachmentErrorException(response: String) : Exception() {
    override val message: String
        get() = "Error! Check your data and try again"

    init {
        println(response)
    }
}