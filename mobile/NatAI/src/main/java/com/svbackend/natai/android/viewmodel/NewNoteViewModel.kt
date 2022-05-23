package com.svbackend.natai.android.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.repository.DiaryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun <T> throttleLatest(
    intervalMs: Long = 300L,
    coroutineScope: CoroutineScope,
    destinationFunction: (T) -> Unit
): (T) -> Unit {
    var throttleJob: Job? = null
    var latestParam: T
    return { param: T ->
        latestParam = param
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                delay(intervalMs)
                latestParam.let(destinationFunction)
            }
        }
    }
}

class NewNoteViewModel(application: Application) : AndroidViewModel(application) {
    val repository: DiaryRepository = (application as DiaryApplication).appContainer.diaryRepository
    val prefs: SharedPreferencesStorage = (application as DiaryApplication).appContainer.sharedPrefs

    val title = mutableStateOf(
        TextFieldValue(prefs.retrieveString("new_note_title") ?: "")
    )
    val content = mutableStateOf(
        TextFieldValue(prefs.retrieveString("new_note_content") ?: "")
    )

    val isLoading = mutableStateOf(false)

    fun saveTitle(title: String) {
        prefs.store("new_note_title", title)
    }

    fun saveContent(content: String) {
        prefs.store("new_note_content", content)
    }

    fun clearStoredData() {
        title.value = TextFieldValue("")
        content.value = TextFieldValue("")

        prefs.let {
            it.remove("new_note_title")
            it.remove("new_note_content")
        }
    }

    suspend fun addNote() {
        isLoading.value = true
        repository.insert(
            Note(
                title = title.value.text,
                content = content.value.text,
            )
        )
        clearStoredData()
        isLoading.value = false
    }
}