package com.svbackend.natai.android.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.entity.Tag
import com.svbackend.natai.android.repository.DiaryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
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

    val note = MutableSharedFlow<LocalNote?>(replay = 1)

    val isLoading = mutableStateOf(false)

    suspend fun saveNote(
        note: LocalNote,
    ) {
        isLoading.value = true

        val entity = Note.create(note)

        entity.update(
            title = title.value.text,
            content = content.value.text,
            actualDate = actualDate.value,
        )
        repository.updateNote(entity)
        repository.deleteTagsByNote(note.id)

        note.tags.forEach {
            repository.insertTag(
                Tag(
                    noteId = note.id,
                    name = it.name,
                    score = it.score,
                )
            )
        }

        isLoading.value = false
    }

    fun actualDateChanged(newDate: LocalDate) {
        actualDate.value = newDate
    }

    fun loadNote(noteId: String) = viewModelScope.launch {
        repository.getNote(noteId).collect {
            val localNote = LocalNote.create(it)
            title.value = TextFieldValue(localNote.title)
            content.value = TextFieldValue(localNote.content)
            actualDate.value = localNote.actualDate
            note.emit(localNote)
        }
    }
}