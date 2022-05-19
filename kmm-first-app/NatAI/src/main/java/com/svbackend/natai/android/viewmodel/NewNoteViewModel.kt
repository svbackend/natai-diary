package com.svbackend.natai.android.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.repository.DiaryRepository

class NewNoteViewModel(application: Application) : AndroidViewModel(application) {
    val repository: DiaryRepository = (application as DiaryApplication).appContainer.diaryRepository

    val title = mutableStateOf(TextFieldValue(""))
    val content = mutableStateOf(TextFieldValue(""))

    val isLoading = mutableStateOf(false)
}