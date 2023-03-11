package com.svbackend.natai.android.service

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import java.io.File

class FileManagerService(
    private val contentResolver: ContentResolver,
    private val filesDir: File
) {
    fun copyFileToInternalStorage(uri: Uri, originalFilename: String): Uri {
        val inputStream = contentResolver.openInputStream(uri)

        if (inputStream == null) {
            throw IllegalArgumentException("$uri - not found")
        }

        val file = File(filesDir, originalFilename)
        val internalUri = file.toUri()

        inputStream.use { input ->
            contentResolver.openOutputStream(internalUri, "w").use { output ->
                if (output == null) {
                    throw IllegalArgumentException("$internalUri - not created")
                }

                input.copyTo(output)
            }
        }

        return internalUri
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