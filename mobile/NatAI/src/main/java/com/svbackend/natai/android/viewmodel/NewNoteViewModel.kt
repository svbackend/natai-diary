package com.svbackend.natai.android.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.repository.DiaryRepository

class NewNoteViewModel(application: Application) : AndroidViewModel(application) {
    val repository: DiaryRepository = (application as DiaryApplication).appContainer.diaryRepository
    val prefs: SharedPreferences = (application as DiaryApplication).appContainer.sharedPrefs

    val title = mutableStateOf(
        TextFieldValue(prefs.getString("new_note_title", null) ?: "")
    )
    val content = mutableStateOf(
        TextFieldValue(prefs.getString("new_note_content", null) ?: "")
    )

    val isLoading = mutableStateOf(false)

    fun saveTitle(title: String) {
        prefs.edit().putString("new_note_title", title).apply()
    }

    fun saveContent(content: String) {
        prefs.edit().putString("new_note_content", content).apply()
    }

    fun clearStoredData() {
        title.value = TextFieldValue("")
        content.value = TextFieldValue("")

        prefs
            .edit()
            .remove("new_note_title")
            .remove("new_note_content")
            .apply()

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