package com.svbackend.natai.android.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.result.UserProfile
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.LoggedUserInfo
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.repository.DiaryRepository
import com.svbackend.natai.android.ui.UserTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    val repository: DiaryRepository = (application as DiaryApplication).appContainer.diaryRepository

    val isLoggedIn = MutableSharedFlow<Boolean>()
    val user = MutableSharedFlow<LoggedUserInfo>()
    val currentTheme = MutableSharedFlow<UserTheme>(replay = 1)
    //val currentRoute = MutableSharedFlow<String?>() // todo

    val notes = repository.notes
    var notesState by mutableStateOf(emptyList<Note>())

    //suspend fun delete(id: String) = repository.delete(repository.getNote(id))

    val selectedNote = MutableSharedFlow<Note?>(replay = 1)

    fun selectNote(id: String) = viewModelScope.launch {
        repository.getNote(id)
            .collect {
                selectedNote.emit(it)
            }
    }

    suspend fun userProfileLoaded(profile: UserProfile) {
        val userInfo = LoggedUserInfo(
            name = profile.name ?: "John Doe",
            email = profile.email ?: "",
            pictureUrl = profile.pictureURL ?: "https://picsum.photos/100/100",
        )
        user.emit(userInfo)
    }

    suspend fun login() {
        isLoggedIn.emit(true)
    }

    suspend fun credsFailure() {
        isLoggedIn.emit(false)
    }

    val isSyncing = MutableSharedFlow<Boolean>()

    suspend fun startSync() {
        isSyncing.emit(true)
    }

    suspend fun finishSync() {
        isSyncing.emit(false)
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
        }
    }

    fun changeTheme(theme: UserTheme) {
        viewModelScope.launch {
            currentTheme.emit(theme)
        }
    }

    init {
        viewModelScope.launch {
            notes.collect {
                notesState = it
            }
        }
    }
}