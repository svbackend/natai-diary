package com.svbackend.natai.android.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.entity.*
import com.svbackend.natai.android.repository.DiaryRepository
import java.time.LocalDate

class NewNoteViewModel(application: Application) : AndroidViewModel(application) {
    val repository: DiaryRepository = (application as DiaryApplication).appContainer.diaryRepository
    val prefs: SharedPreferences = (application as DiaryApplication).appContainer.sharedPrefs

    val title = mutableStateOf(
        TextFieldValue(prefs.getString("new_note_title", null) ?: "")
    )
    val content = mutableStateOf(
        TextFieldValue(prefs.getString("new_note_content", null) ?: "")
    )
    val tagsFieldValue = mutableStateOf(
        TextFieldValue("")
    )
    val actualDate = mutableStateOf(LocalDate.now())
    val tags = mutableStateOf(emptyList<TagEntityDto>())

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
        tagsFieldValue.value = TextFieldValue("")

        prefs
            .edit()
            .remove("new_note_title")
            .remove("new_note_content")
            .apply()

    }

    suspend fun addNote() {
        isLoading.value = true
        val note = LocalNote(
            title = title.value.text,
            content = content.value.text,
            actualDate = actualDate.value,
            tags = tags.value
        )

        repository.insertNoteAndSync(note)
        clearStoredData()
        isLoading.value = false
    }

    fun actualDateChanged(newDate: LocalDate) {
        actualDate.value = newDate
    }

    fun addTag(tag: TagEntityDto) {
        if (!tags.value.contains(tag)) {
            tags.value = tags.value.plus(tag)
        }
    }

    fun deleteTag(tag: TagEntityDto) {
        tags.value = tags.value.minus(tag)
    }
}