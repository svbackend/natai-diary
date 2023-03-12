package com.svbackend.natai.android.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.entity.*
import com.svbackend.natai.android.http.exception.DownloadAttachmentErrorException
import com.svbackend.natai.android.repository.DiaryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate

class EditNoteViewModel(application: Application) : AndroidViewModel(application) {
    val repository: DiaryRepository = (application as DiaryApplication).appContainer.diaryRepository
    val apiClient = (application as DiaryApplication).appContainer.apiClient
    val fileManager = (application as DiaryApplication).appContainer.fileManager

    val actualDate = mutableStateOf(LocalDate.now())

    val title = mutableStateOf(
        TextFieldValue("")
    )
    val content = mutableStateOf(
        TextFieldValue("")
    )
    val tagsFieldValue = mutableStateOf(
        TextFieldValue("")
    )

    val tags = mutableStateOf(emptyList<TagEntityDto>())

    val existingAttachments = mutableStateOf(
        emptyList<ExistingAttachmentDto>()
    )

    val note = MutableSharedFlow<LocalNote?>(replay = 1)

    val isLoading = mutableStateOf(false)

    val tagsSuggestions = repository.notes.map { notes -> Tag.getMostFrequentlyUsedTags(notes) }

    suspend fun saveNote(
        note: LocalNote,
        existingAttachments: List<ExistingAttachmentDto>,
        addedFiles: List<AddedFile>
    ) {
        isLoading.value = true

        val notAddedTag = TagEntityDto.createOrNull(tagsFieldValue.value.text)

        if (notAddedTag != null) {
            tags.value = tags.value + notAddedTag
        }

        val newAttachments = addedFiles.map { file ->
            AttachmentEntityDto(
                uri = file.uri,
                filename = file.originalFilename,
                cloudAttachmentId = file.cloudAttachmentId,
            )
        }

        val combinedAttachments = newAttachments + existingAttachments.map {
            AttachmentEntityDto(
                uri = it.uri,
                filename = it.filename,
                cloudAttachmentId = it.cloudAttachmentId,
            )
        }

        val updatedNote = note.update(
            title = title.value.text,
            content = content.value.text,
            actualDate = actualDate.value,
            tags = tags.value,
            attachments = combinedAttachments
        )

        repository.updateNoteAndSync(updatedNote)

        isLoading.value = false
        clearStoredData()
    }

    fun actualDateChanged(newDate: LocalDate) {
        actualDate.value = newDate
    }

    fun addTag(tag: TagEntityDto) {
        if (!tags.value.contains(tag)) {
            tags.value = tags.value.plus(tag)
        } else replaceTag(tag)
    }

    fun deleteTag(tag: TagEntityDto) {
        tags.value = tags.value.minus(tag)
    }

    fun replaceTag(tag: TagEntityDto) {
        tags.value = tags.value.minus(tag)
        tags.value = tags.value.plus(tag)
    }

    fun loadNote(noteId: String) = viewModelScope.launch {
        repository.getNote(noteId).collect {
            val localNote = LocalNote.create(it)
            title.value = TextFieldValue(localNote.title)
            content.value = TextFieldValue(localNote.content)
            actualDate.value = localNote.actualDate
            tags.value = it.tags.map { tag ->
                TagEntityDto.create(tag)
            }
            note.emit(localNote)

            loadAttachments(localNote)
        }
    }

    suspend fun loadAttachments(note: LocalNote) {
        println("loadAttachments")

        // res/raw/placeholder.png
        val placeholderUri =
            Uri.parse("android.resource://com.svbackend.natai.android/raw/placeholder")

        val localAttachments = note.attachments.map { attachment ->
            val uri = attachment.uri ?: placeholderUri
            ExistingAttachmentDto.create(attachment, uri)
        }

        val attachmentsIdsWithoutUri = note.attachments
            .filter { it.uri == null || !fileManager.isFileExists(it.uri) }
            .mapNotNull { it.cloudAttachmentId }

        if (note.cloudId == null || attachmentsIdsWithoutUri.isEmpty()) {
            existingAttachments.value = localAttachments
            println("NOTEID / ATTACHMENTS WITHOUT URIS: ${note.cloudId} / $attachmentsIdsWithoutUri")
            println("EXISTING ATTACHMENTS 1: ${existingAttachments.value}")
            return
        }

        try {
            val cloudAttachments = apiClient
                .getAttachmentsByNote(note.cloudId, attachmentsIdsWithoutUri)
                .attachments

            println(cloudAttachments)

            val downloadedUrisMap = mutableMapOf<String, Uri>() // cloudAttachmentId to uri
            val cacheDir = getApplication<DiaryApplication>().cacheDir

            attachmentsIdsWithoutUri.forEach {
                val cloudAttachment = cloudAttachments.find { cloudAttachment ->
                    cloudAttachment.attachmentId == it
                }

                if (cloudAttachment != null) {
                    val uri = apiClient.downloadAttachment(cacheDir, cloudAttachment.signedUrl)
                    downloadedUrisMap[it] = uri
                }
            }

            println(downloadedUrisMap)

            existingAttachments.value = localAttachments.map { localAttachment ->
                val uri = downloadedUrisMap[localAttachment.cloudAttachmentId]
                if (uri != null) {
                    localAttachment.copy(uri = uri)
                } else localAttachment
            }

        } catch (e: Throwable) {
            e.printStackTrace()
            existingAttachments.value = localAttachments
        }

        println("EXISTING ATTACHMENTS 2: ${existingAttachments.value}")
    }

    private fun clearStoredData() {
        title.value = TextFieldValue("")
        content.value = TextFieldValue("")
        tagsFieldValue.value = TextFieldValue("")
    }

    fun deleteExistingAttachment(attachment: ExistingAttachmentDto) {
        existingAttachments.value = existingAttachments.value.filter {
            it.uri != attachment.uri
        }
    }
}