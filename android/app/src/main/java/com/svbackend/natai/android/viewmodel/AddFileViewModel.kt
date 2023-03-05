package com.svbackend.natai.android.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel

data class AddedFile(
    val uri: Uri,
    val originalFilename: String,
    val shortFilename: String,
    val isUploading: Boolean = false,
    val error: String? = null,
    val progress: Float = 0f,
) {
    val isPending: Boolean
        get() = !isUploading && error == null

    val isUploaded: Boolean
        get() = progress == 1f && error == null
}

class AddFileViewModel(application: Application) : AndroidViewModel(application) {
    val contentResolver: ContentResolver = application.contentResolver

    val isAddFileDialogOpen = mutableStateOf(false)

    val addedFiles = mutableStateOf(
        emptyList<AddedFile>()
    )

    fun onDelete(file: AddedFile) {
        addedFiles.value = addedFiles.value.filter { it.uri != file.uri }
    }

    fun onAdd(uris: List<Uri>) {
        addedFiles.value = addedFiles.value + mapUrisToAddedFiles(uris)
        onOpen()
    }

    fun onClose() {
        isAddFileDialogOpen.value = false
    }

    fun onOpen() {
        isAddFileDialogOpen.value = true
    }

    fun mapUriToAddedFile(uri: Uri): AddedFile {
        val originalFilename = getOriginalFilename(uri)
        val shortFilename = getShortenedFilename(originalFilename)
        return AddedFile(uri, originalFilename, shortFilename)
    }

    fun mapUrisToAddedFiles(uris: List<Uri>): List<AddedFile> {
        return uris.map { mapUriToAddedFile(it) }
    }

    fun getOriginalFilename(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex("_display_name")
                if (nameIndex != -1) {
                    return it.getString(nameIndex)
                }
                return uri.lastPathSegment ?: "ERROR1"
            }
        }
        return "ERROR2"
    }

    fun getShortenedFilename(originalFilename: String): String {
        val maxLength = 14

        if (originalFilename.length <= maxLength) {
            return originalFilename
        }

        val ext = originalFilename.substringAfterLast('.')
        val filename = originalFilename.substringBeforeLast('.')

        val namePartLength = maxLength / 2
        val namePart1 = filename.substring(0, namePartLength)
        val namePart2 = filename.substring(filename.length - namePartLength, filename.length)

        return "$namePart1...$namePart2.$ext"
    }
}