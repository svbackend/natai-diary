package com.svbackend.natai.android.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.service.FileManagerService
import kotlinx.coroutines.launch

data class AddedFile(
    val uri: Uri,
    val originalFilename: String,
    val contentType: String,
    val shortFilename: String,
    val isUploading: Boolean = false,
    val error: String? = null,
    val progress: Double = .0,
    val cloudAttachmentId: String? = null,
) {
    val isPending: Boolean
        get() = !isUploading && error == null

    val isUploaded: Boolean
        get() = progress == 1.0 && error == null
}

class AddFileViewModel(application: Application) : AndroidViewModel(application) {
    val contentResolver: ContentResolver = application.contentResolver
    val apiClient: ApiClient = (application as DiaryApplication).appContainer.apiClient
    val fileManager: FileManagerService = (application as DiaryApplication).appContainer.fileManager

    val isAddFileDialogOpen = mutableStateOf(false)

    val addedFiles = mutableStateOf(
        emptyList<AddedFile>()
    )

    fun onDelete(file: AddedFile) {
        addedFiles.value = addedFiles.value.filter { it.uri != file.uri }
    }

    fun onAdd(uris: List<Uri>) {
        val newlyAddedFiles = mapUrisToAddedFiles(uris)
        addedFiles.value = addedFiles.value + newlyAddedFiles
        onOpen()

        viewModelScope.launch {
            enqueueUpload(newlyAddedFiles)
        }
    }

    fun updateFile(file: AddedFile) {
        addedFiles.value = addedFiles.value.map {
            if (it.uri == file.uri) {
                file
            } else {
                it
            }
        }
    }

    private suspend fun enqueueUpload(files: List<AddedFile>) {
        files.forEach { file ->
            try {
                val pendingAttachment = apiClient.getAttachmentSignedUrl(file.originalFilename)

                updateFile(
                    // todo move to method
                    file.copy(
                        isUploading = true,
                        cloudAttachmentId = pendingAttachment.attachmentId,
                    )
                )

                val resolvedFileInput = contentResolver.openInputStream(file.uri)

                if (resolvedFileInput == null) {
                    println("------- resolvedFileInput == null")
                    updateFile(
                        // todo move to method
                        file.copy(
                            error = "File not found",
                            isUploading = false,
                        )
                    )
                } else {
                    println("------- START UPLOADING")
                    resolvedFileInput.use { inputStream ->
                        apiClient.uploadFile(
                            inputStream = inputStream,
                            pendingAttachment.uploadUrl, onProgress = {
                                updateFile(
                                    // todo move to method
                                    file.copy(
                                        progress = it,
                                    )
                                )
                            }) {
                            updateFile(
                                // todo move to method
                                file.copy(
                                    isUploading = false,
                                    progress = 1.0,
                                    error = null,
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                println("------- UPLOADING ERROR")
                e.printStackTrace()
                updateFile(
                    // todo move to method
                    file.copy(
                        error = e.message,
                        isUploading = false,
                    )
                )
            }
        }
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
        val contentType = contentResolver.getType(uri) ?: "application/octet-stream"
        return AddedFile(
            uri = uri,
            originalFilename = originalFilename,
            contentType = contentType,
            shortFilename = shortFilename
        )
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