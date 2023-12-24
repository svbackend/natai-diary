package com.svbackend.natai.android.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.entity.AttachmentEntityDto
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.entity.NewAttachmentDto
import com.svbackend.natai.android.entity.Tag
import com.svbackend.natai.android.entity.TagEntityDto
import com.svbackend.natai.android.repository.DiaryRepository
import com.svbackend.natai.android.service.TitleGenerator
import com.svbackend.natai.android.utils.getUserCloudId
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate

class NewNoteViewModel(application: Application) : AndroidViewModel(application) {
    val repository: DiaryRepository = (application as DiaryApplication).appContainer.diaryRepository
    val userRepository = (application as DiaryApplication).appContainer.userRepository
    val prefs: SharedPreferences = (application as DiaryApplication).appContainer.sharedPrefs
    val titleGenerator: TitleGenerator =
        (application as DiaryApplication).appContainer.titleGenerator

    val api = (application as DiaryApplication).appContainer.apiClient

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

    val tagsSuggestions = repository.notes.map { notes -> Tag.getMostFrequentlyUsedTags(notes) }

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

    suspend fun addNote(addedFiles: List<NewAttachmentDto>) {
        isLoading.value = true

        val cloudUserId = prefs.getUserCloudId()
        val appendIfPossible =
            title.value.text.isEmpty() && content.value.text.isEmpty() && addedFiles.isEmpty()

        val title = title.value.text.ifEmpty {
            titleGenerator.generateTitle()
        }

        val attachments = addedFiles.map { file ->
            AttachmentEntityDto.create(file)
        }

        var appended = false
        if (appendIfPossible) {
            val lastNote = repository.getLastNoteByActualDate(actualDate.value!!)

            if (lastNote != null) {
                val existingNote = LocalNote
                    .create(lastNote)
                    .updateTags(tags.value)
                repository.updateNoteAndSync(existingNote)
                appended = true
            }
        }

        if (!appended) {
            val note = LocalNote(
                title = title,
                content = content.value.text,
                actualDate = actualDate.value,
                tags = tags.value,
                attachments = attachments,
                cloudUserId = cloudUserId,
            )

            repository.insertNoteAndSync(note)
        }


        clearStoredData()
        isLoading.value = false
    }

    fun actualDateChanged(newDate: LocalDate) {
        actualDate.value = newDate
        loadWeatherTag()
    }

    fun addTag(tag: TagEntityDto) {
        if (!tags.value.contains(tag)) {
            tags.value = tags.value.plus(tag)
        } else replaceTag(tag)
    }

    fun deleteTag(tag: TagEntityDto) {
        tags.value = tags.value.minus(tag)
    }

    fun replaceTag(tag: TagEntityDto) {
        tags.value = tags.value.minus(tag)
        tags.value = tags.value.plus(tag)
    }

    private fun loadWeatherTag() {
        val cloudUserId = prefs.getUserCloudId() ?: return

        viewModelScope.launch {
            val user = userRepository.getUserByCloudIdSync(cloudUserId) ?: return@launch

            val cityId = user.cloudCityId ?: return@launch

            try {
                val weatherScore = api.loadWeatherTag(cityId, actualDate.value)

                addTag(
                    TagEntityDto(
                        tag = "weather",
                        score = weatherScore
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    init {
        loadWeatherTag()
    }
}