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
import com.svbackend.natai.android.service.ApiSyncService
import com.svbackend.natai.android.ui.UserTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    val diaryRepository: DiaryRepository =
        (application as DiaryApplication).appContainer.diaryRepository
    val userRepository: UserRepository =
        (application as DiaryApplication).appContainer.userRepository

    val apiSyncService: ApiSyncService =
        (application as DiaryApplication).appContainer.apiSyncService

    val prefs = (application as DiaryApplication).appContainer.sharedPrefs

    val userCloudId = MutableSharedFlow<String?>(replay = 1)
    val userCloudIdState = mutableStateOf<String?>(null)
    val users = userRepository.users
    val user = MutableSharedFlow<User?>(replay = 1)
    var userState by mutableStateOf<User?>(null)

    val currentTheme = MutableSharedFlow<UserTheme>(replay = 1)

    val notes = diaryRepository.notes
    var notesState by mutableStateOf(emptyList<LocalNote>()) // current user's notes
    var allNotesState by mutableStateOf(emptyList<LocalNote>())

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

    suspend fun sync() {
        startSync()
        try {
            apiSyncService.syncNotes()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            finishSync()
        }
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

    suspend fun setUserCloudId(cloudId: String) {
        this.userCloudId.emit(cloudId)
    }

    suspend fun setUser(user: User) {
        prefs.edit()
            .putString("api_token", user.apiToken)
            .putString("cloud_id", user.cloudId)
            .apply()

        diaryRepository.assignNotesToUser(user.cloudId)

        setUserCloudId(user.cloudId)

        sync()
    }

    private val jobs = mutableListOf<Job>()

    init {
        viewModelScope.launch {
            userCloudId.collect {
                clearSubscribers()
                userCloudIdState.value = it
                setupSubscribers()
            }
        }

        setupSubscribers()
    }

    private fun setupSubscribers() {
        val usersSubscriberJob = viewModelScope.launch {
            users.collect {
                if (userCloudIdState.value != null) {
                    userState = it.find { user -> user.cloudId == userCloudIdState.value }
                } else {
                    userState = null
                }
            }
        }

        val notesSubscriberJob = viewModelScope.launch {
            notes.collect {
                allNotesState = it
                notesState =
                    it.filter { note -> note.cloudUserId == null || note.cloudUserId == userCloudIdState.value }
            }
        }

        jobs.add(usersSubscriberJob)
        jobs.add(notesSubscriberJob)
    }

    private fun clearSubscribers() {
        jobs.forEach { job -> job.cancel() }
        jobs.clear()
    }
}