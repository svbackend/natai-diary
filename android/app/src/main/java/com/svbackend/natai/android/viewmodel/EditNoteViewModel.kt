package com.svbackend.natai.android.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.entity.*
import com.svbackend.natai.android.repository.DiaryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate

class EditNoteViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "EditNoteViewModel"
    val repository: DiaryRepository = (application as DiaryApplication).appContainer.diaryRepository
    val attachmentsLoader = (application as DiaryApplication).appContainer.attachmentsLoader

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

    val existingLocalAttachments = mutableStateOf(
        emptyList<ExistingLocalAttachmentDto>()
    )

    val note = MutableSharedFlow<LocalNote?>(replay = 1)

    val isLoading = mutableStateOf(false)

    val tagsSuggestions = repository.notes.map { notes -> Tag.getMostFrequentlyUsedTags(notes) }

    suspend fun saveNote(
        note: LocalNote,
        existingAttachments: List<ExistingAttachmentDto>,
        addedFiles: List<NewAttachmentDto>
    ) {
        isLoading.value = true

        val notAddedTag = TagEntityDto.createOrNull(tagsFieldValue.value.text)

        if (notAddedTag != null) {
            tags.value = tags.value + notAddedTag
        }

        val newAttachments = addedFiles.map { file ->
            AttachmentEntityDto.create(file)
        }

        val existingLocalAttachments = existingLocalAttachments.value.map {
            AttachmentEntityDto(
                uri = it.uri,
                previewUri = it.previewUri,
                filename = it.filename,
                cloudAttachmentId = null
            )
        }

        val combinedAttachments =
            newAttachments + existingLocalAttachments + existingAttachments.map {
                AttachmentEntityDto.create(it)
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
        Log.v(TAG, "=== EDIT NOTE - loadNote $noteId ===")
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

    private suspend fun loadAttachments(note: LocalNote) {
        existingAttachments.value = attachmentsLoader.loadAttachments(note)

        existingLocalAttachments.value = note.attachments
            .filter { it.cloudAttachmentId == null }
            .mapNotNull { attachment ->
                if (attachment.uri == null) {
                    return@mapNotNull null
                }
                ExistingLocalAttachmentDto(
                    filename = attachment.filename,
                    uri = attachment.uri,
                    previewUri = attachment.previewUri
                )
            }
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