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

    val note = MutableSharedFlow<Note?>(replay = 1)

    val isLoading = mutableStateOf(false)

    suspend fun saveNote(
        note: Note,
    ) {
        isLoading.value = true

        note.update(
            title = title.value.text,
            content = content.value.text,
            actualDate = actualDate.value,
        )
        repository.update(note)

        isLoading.value = false
    }

    fun actualDateChanged(newDate: LocalDate) {
        actualDate.value = newDate
    }

    fun loadNote(noteId: String) = viewModelScope.launch {
        repository.getNote(noteId).collect {
            title.value = TextFieldValue(it.title)
            content.value = TextFieldValue(it.content)
            actualDate.value = it.actualDate
            note.emit(it)
        }
    }
}