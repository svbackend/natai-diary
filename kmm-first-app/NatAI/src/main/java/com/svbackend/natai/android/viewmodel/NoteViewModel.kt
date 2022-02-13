package com.svbackend.natai.android.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.repository.IDiaryRepository
import kotlinx.coroutines.flow.MutableSharedFlow

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    val repository: IDiaryRepository = (application as DiaryApplication).appContainer.diaryRepository

    val notes = repository.notes

    suspend fun getNote(id: String) = repository.getNote(id)
    suspend fun delete(id: String) = repository.delete(repository.getNote(id))

    private val selectedNote = MutableSharedFlow<Note?>()

    suspend fun selectNote(id: String) = selectedNote.emit(getNote(id))
}