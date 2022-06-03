package com.svbackend.natai.android.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.repository.DiaryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class EditNoteViewModel(application: Application) : AndroidViewModel(application) {
    val repository: DiaryRepository = (application as DiaryApplication).appContainer.diaryRepository

    val title = mutableStateOf(
        TextFieldValue("")
    )
    val content = mutableStateOf(
        TextFieldValue("")
    )

    val note = MutableSharedFlow<Note?>(replay = 1)

    val isLoading = mutableStateOf(false)

    suspend fun saveNote(
        note: Note,
        newTitle: String,
        newContent: String,
    ) {
        isLoading.value = true

        note.update(
            title = newTitle,
            content = newContent,
        )
        repository.update(note)

        isLoading.value = false


    }

    fun loadNote(noteId: String) = viewModelScope.launch {
        repository.getNote(noteId).collect {
            title.value = TextFieldValue(it.title)
            content.value = TextFieldValue(it.content)
            note.emit(it)
        }
    }
}