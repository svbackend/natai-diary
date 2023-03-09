package com.svbackend.natai.android.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.entity.Tag
import com.svbackend.natai.android.entity.TagEntityDto
import com.svbackend.natai.android.repository.DiaryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate

class EditNoteViewModel(application: Application) : AndroidViewModel(application) {
    val repository: DiaryRepository = (application as DiaryApplication).appContainer.diaryRepository

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

    val note = MutableSharedFlow<LocalNote?>(replay = 1)

    val isLoading = mutableStateOf(false)

    val tagsSuggestions = repository.notes.map { notes -> Tag.getMostFrequentlyUsedTags(notes) }

    suspend fun saveNote(
        note: LocalNote,
    ) {
        isLoading.value = true

        val notAddedTag = TagEntityDto.createOrNull(tagsFieldValue.value.text)

        if (notAddedTag != null) {
            tags.value = tags.value + notAddedTag
        }

        val updatedNote = note.update(
            title = title.value.text,
            content = content.value.text,
            actualDate = actualDate.value,
            tags = tags.value,
            attachments = emptyList()
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
        }
    }

    private fun clearStoredData() {
        title.value = TextFieldValue("")
        content.value = TextFieldValue("")
        tagsFieldValue.value = TextFieldValue("")
    }
}