package com.svbackend.natai.android.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.repository.DiaryRepository
import kotlinx.coroutines.flow.MutableSharedFlow

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    val repository: DiaryRepository = (application as DiaryApplication).appContainer.diaryRepository

    val isLoggedIn = MutableSharedFlow<Boolean>(replay = 1)
    //val currentRoute = MutableSharedFlow<String?>() // todo

    val notes = repository.notes

    suspend fun getNote(id: String) = repository.getNote(id)
    suspend fun delete(id: String) = repository.delete(repository.getNote(id))

    val selectedNote = MutableSharedFlow<Note?>()

    suspend fun selectNote(id: String) = selectedNote.emit(getNote(id))

    suspend fun login() {
        isLoggedIn.emit(true)
    }

    suspend fun credsFailure() {
        isLoggedIn.emit(false)
    }
}