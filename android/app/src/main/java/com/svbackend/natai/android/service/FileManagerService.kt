package com.svbackend.natai.android.service

import android.content.ContentResolver
import android.net.Uri
import androidx.core.net.toFile
import java.io.File

class FileManagerService(
    private val contentResolver: ContentResolver,
    private val filesDir: File
) {
    fun copyFileToInternalStorage(uri: Uri): Uri {
        val inputStream = contentResolver.openInputStream(uri)

        if (inputStream == null) {
            throw IllegalArgumentException("$uri - not found")
        }

        val fileName = uri.lastPathSegment ?: "file"
        val file = File(filesDir, fileName)
        inputStream.use { input ->
            file.outputStream().use { input.copyTo(it) }
        }
        return Uri.fromFile(file)
    }

    fun deleteFile(uri: Uri) {
        try {
            uri.toFile().delete()
        } catch (e: Exception) {
            println("------- DELETE FILE ERROR $uri")
            e.printStackTrace()
        }
    }
}