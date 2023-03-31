package com.svbackend.natai.android.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.entity.ExistingAttachmentDto
import com.svbackend.natai.android.entity.ExistingLocalAttachmentDto
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.entity.User
import com.svbackend.natai.android.query.UserQueryException
import com.svbackend.natai.android.repository.DiaryRepository
import com.svbackend.natai.android.repository.UserRepository
import com.svbackend.natai.android.service.ApiSyncService
import com.svbackend.natai.android.ui.UserTheme
import com.svbackend.natai.android.utils.getLastSyncTime
import com.svbackend.natai.android.utils.updateLastSyncTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


class NoteViewModel(application: Application) : AndroidViewModel(application) {
    val TAG = "NoteViewModel"

    val diaryRepository: DiaryRepository =
        (application as DiaryApplication).appContainer.diaryRepository
    val userRepository: UserRepository =
        (application as DiaryApplication).appContainer.userRepository

    val apiSyncService: ApiSyncService =
        (application as DiaryApplication).appContainer.apiSyncService

    val attachmentsLoader = (application as DiaryApplication).appContainer.attachmentsLoader

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
    val selectedNoteAttachments = mutableStateOf(emptyList<ExistingLocalAttachmentDto>())
    val selectedAttachment = mutableStateOf<ExistingLocalAttachmentDto?>(null)
    val isLoadingAttachments = mutableStateOf(false)

    fun selectNote(id: String) = viewModelScope.launch {
        selectedNote.emit(null)
        selectedNoteAttachments.value = emptyList()

        Log.v(TAG, "=== VIEW NOTE === selectNote: $id")

        diaryRepository.getNote(id)
            .collect {
                val localNote = LocalNote.create(it)
                selectedNote.emit(localNote)
                isLoadingAttachments.value = true
                try {
                    loadAttachments(localNote)
                } finally {
                    isLoadingAttachments.value = false
                }
            }
    }

    fun selectAttachment(attachment: ExistingLocalAttachmentDto) {
        selectedAttachment.value = attachment
    }

    fun clearSelectedAttachment() {
        selectedAttachment.value = null
    }

    private suspend fun loadAttachments(note: LocalNote) {
        var existingAttachments: List<ExistingAttachmentDto> = emptyList();
        try {
            existingAttachments = attachmentsLoader.loadAttachments(note)
            Log.v(TAG, "=== existingAttachments: $existingAttachments")
        } catch (e: Exception) {
            Log.v(TAG, "Failed to load attachments", e)
        }

        try {
            val loadedAttachments = attachmentsLoader.loadLocalAttachments(note)
            Log.v(TAG, "=== loadedAttachments: $loadedAttachments")
            val mergedAttachments = mergeLoadedAttachments(existingAttachments, loadedAttachments)
            Log.v(TAG, "=== MERGED ATTACHMENTS: $mergedAttachments")
            selectedNoteAttachments.value = mergedAttachments
        } catch (e: Exception) {
            selectedNoteAttachments.value = emptyList()
            Log.v(TAG, "Failed to load local attachments", e)
        }
    }

    private fun mergeLoadedAttachments(
        att1: List<ExistingAttachmentDto>,
        att2: List<ExistingLocalAttachmentDto>
    ): List<ExistingLocalAttachmentDto> {
        return att2.map {
            val existing = att1.find { att -> att.cloudAttachmentId == it.cloudAttachmentId }
            if (existing != null) {
                it.copy(
                    uri = existing.uri ?: it.uri,
                    previewUri = existing.previewUri ?: it.previewUri,
                )
            } else {
                it
            }
        }
    }

    val isSyncing = MutableSharedFlow<Boolean>()

    suspend fun startSync() {
        isSyncing.emit(true)
    }

    suspend fun finishSync() {
        isSyncing.emit(false)
    }

    fun sync() = viewModelScope.launch {
        startSync()
        val lastSyncTime = prefs.getLastSyncTime()
        try {
            apiSyncService.syncNotes(lastSyncTime)
            prefs.updateLastSyncTime()
        } catch (userError: UserQueryException) {
            logout { }
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

    suspend fun setUserCloudId(cloudId: String?) {
        this.userCloudId.emit(cloudId)
    }

    suspend fun setUser(user: User) {
        prefs.edit()
            .putString("api_token", user.apiToken)
            .putString("cloud_id", user.cloudId)
            .apply()

        setUserCloudId(user.cloudId)

        diaryRepository.assignNotesToUser(user.cloudId)

        viewModelScope.launch {
            sync()
        }
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

    suspend fun logout(onLogout: () -> Unit) {
        prefs
            .edit()
            .remove("api_token")
            .remove("cloud_id")
            .apply()

        setUserCloudId(null)

        onLogout()
    }

    fun selectNextAttachment() {
        val attachments = selectedNoteAttachments.value
        val currentAttachment = selectedAttachment.value

        if (attachments.isEmpty()) {
            return
        }

        if (currentAttachment == null) {
            selectAttachment(attachments.first())
            return
        }

        val currentIndex = attachments.indexOf(currentAttachment)
        val nextIndex = if (currentIndex == attachments.lastIndex) 0 else currentIndex + 1

        selectAttachment(attachments[nextIndex])
    }

    fun selectPrevAttachment() {
        val attachments = selectedNoteAttachments.value
        val currentAttachment = selectedAttachment.value

        if (attachments.isEmpty()) {
            return
        }

        if (currentAttachment == null) {
            selectAttachment(attachments.first())
            return
        }

        val currentIndex = attachments.indexOf(currentAttachment)
        val nextIndex = if (currentIndex == 0) attachments.lastIndex else currentIndex - 1

        selectAttachment(attachments[nextIndex])
    }
}