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
import com.svbackend.natai.android.utils.hasInternetConnection
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
        get() = !isUploading && error == null && progress == .0

    val isUploaded: Boolean
        get() = progress == 1.0 && error == null
}

class AddFileViewModel(application: Application) : AndroidViewModel(application) {
    val contentResolver: ContentResolver = application.contentResolver
    val apiClient: ApiClient = (application as DiaryApplication).appContainer.apiClient
    val fileManager: FileManagerService = (application as DiaryApplication).appContainer.fileManager
    val connectivityManager = (application as DiaryApplication).appContainer.connectivityManager

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

        if (!hasInternetConnection(connectivityManager)) {
            newlyAddedFiles.forEach { file ->
                updateFile(
                    file.copy(
                        error = "No internet",
                    )
                )
            }
        } else {
            viewModelScope.launch {
                enqueueUpload(newlyAddedFiles)
            }
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

                println("------- GOT ATTACHMENT ID: ${pendingAttachment.attachmentId}")
                println(pendingAttachment)

                // todo move to method
                var updatedFile = file.copy(
                    isUploading = true,
                    cloudAttachmentId = pendingAttachment.attachmentId,
                )

                updateFile(updatedFile)

                val resolvedFileInput = contentResolver.openInputStream(file.uri)

                if (resolvedFileInput == null) {
                    updatedFile = updatedFile.copy(
                        error = "File not found",
                        isUploading = false,
                    )
                    updateFile(updatedFile)
                } else {
                    println("------- START UPLOADING")
                    resolvedFileInput.use { inputStream ->
                        apiClient.uploadFile(
                            inputStream = inputStream,
                            contentType = file.contentType,
                            pendingAttachment.uploadUrl, onProgress = {
                                updatedFile = updatedFile.copy(
                                    progress = it,
                                )
                                updateFile(updatedFile)
                            }) {
                            updatedFile = updatedFile.copy(
                                isUploading = false,
                                progress = 1.0,
                                error = null,
                            )
                            updateFile(updatedFile)
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

    fun copyFilesToInternalStorage(): List<AddedFile> {
        // we are copying files to internal storage to make sure that they are not deleted before cloud sync
        return addedFiles.value.mapNotNull { file ->
            try {
                val newUri = fileManager.copyFileToInternalStorage(file.uri, file.originalFilename)
                return@mapNotNull file.copy(
                    uri = newUri
                )
            } catch (e: Exception) {
                e.printStackTrace()
                return@mapNotNull null
            }
        }
    }
}