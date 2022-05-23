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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    val repository: DiaryRepository = (application as DiaryApplication).appContainer.diaryRepository

    val isLoggedIn = MutableSharedFlow<Boolean>()
    val user = MutableSharedFlow<LoggedUserInfo>()
    //val currentRoute = MutableSharedFlow<String?>() // todo

    val notes = repository.notes
    var notesState by mutableStateOf(emptyList<Note>())

    suspend fun getNote(id: String) = repository.getNote(id)
    suspend fun delete(id: String) = repository.delete(repository.getNote(id))

    val selectedNote = MutableSharedFlow<Note?>()

    suspend fun selectNote(id: String) = selectedNote.emit(getNote(id))

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

    init {
        viewModelScope.launch {
            notes.collect {
                notesState = it
            }
        }
    }
}