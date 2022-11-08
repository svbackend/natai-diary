package com.svbackend.natai.android.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.entity.User
import com.svbackend.natai.android.repository.DiaryRepository
import com.svbackend.natai.android.repository.UserRepository
import com.svbackend.natai.android.ui.UserTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    val diaryRepository: DiaryRepository =
        (application as DiaryApplication).appContainer.diaryRepository
    val userRepository: UserRepository =
        (application as DiaryApplication).appContainer.userRepository

    val isLoggedIn = MutableSharedFlow<Boolean>()
    val userCloudId = MutableSharedFlow<String?>()
    val user = MutableSharedFlow<User?>()
    val currentTheme = MutableSharedFlow<UserTheme>(replay = 1)
    //val currentRoute = MutableSharedFlow<String?>() // todo

    val notes = diaryRepository.notes
    var notesState by mutableStateOf(emptyList<LocalNote>())

    val selectedNote = MutableSharedFlow<LocalNote?>(replay = 1)

    fun selectNote(id: String) = viewModelScope.launch {
        diaryRepository.getNote(id)
            .collect {
                selectedNote.emit(LocalNote.create(it))
            }
    }

    val isSyncing = MutableSharedFlow<Boolean>()

    suspend fun startSync() {
        isSyncing.emit(true)
    }

    suspend fun finishSync() {
        isSyncing.emit(false)
    }

    fun deleteNote(note: LocalNote) {
        viewModelScope.launch {
            diaryRepository.deleteNoteAndSync(note)
        }
    }

    fun changeTheme(theme: UserTheme) {
        viewModelScope.launch {
            currentTheme.emit(theme)
        }
    }

    suspend fun loadCurrentUser() {
        this.userCloudId.collect { userCloudId ->
            if (userCloudId != null) {
                val userFlow = userRepository.getUserByCloudId(userCloudId)

                userFlow.collect { currentUser ->
                    user.emit(currentUser)
                }
            }
        }
    }

    suspend fun setUserCloudId(cloudId: String) {
        this.userCloudId.emit(cloudId)
    }

    init {
        viewModelScope.launch {
            notes.collect {
                notesState = it
            }
        }
    }
}