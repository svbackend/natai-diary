package com.svbackend.natai.android.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svbackend.natai.android.R
import com.svbackend.natai.android.ui.AppDateRow
import com.svbackend.natai.android.ui.NPrimaryButton
import com.svbackend.natai.android.ui.NTextField
import com.svbackend.natai.android.ui.NTextarea
import com.svbackend.natai.android.ui.component.TagsAndFilesRow
import com.svbackend.natai.android.viewmodel.AddFileViewModel
import com.svbackend.natai.android.viewmodel.EditNoteViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun EditNoteScreen(
    vm: EditNoteViewModel,
    addFileVm: AddFileViewModel = viewModel(),
    onSuccess: () -> Unit
) {
    val note = vm.note.collectAsState(initial = null).value

    val tagsSuggestions by vm.tagsSuggestions.collectAsState(initial = emptyList())

    if (note == null) {
        LoadingScreen()
        return
    }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun saveNote(): () -> Unit {
        if (vm.title.value.text.isEmpty() || vm.content.value.text.isEmpty()) {
            return {
                Toast
                    .makeText(context, "Title and content are required", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return {
            scope.launch {
                val existingAttachments = vm.existingAttachments.value
                val addedFiles = addFileVm.processAddedFiles()

                vm.saveNote(
                    note = note,
                    existingAttachments = existingAttachments,
                    addedFiles = addedFiles
                )
                onSuccess()
            }
        }
    }

    val actualDate = vm.actualDate.value
    val onDateChange = { date: LocalDate ->
        vm.actualDateChanged(date)
    }
    val tags by vm.tags
    val tagsValue = vm.tagsFieldValue.value

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.editNote),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            )
            AppDateRow(actualDate = actualDate, onDateChange = onDateChange)
            NTextField(
                value = vm.title.value,
                label = stringResource(R.string.noteTitle),
                onChange = {
                    vm.title.value = it
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    autoCorrect = true
                )
            )
            NTextarea(
                value = vm.content.value,
                label = stringResource(R.string.noteContent),
                onChange = {
                    vm.content.value = it
                }
            )
            TagsAndFilesRow(
                tagsSuggestions = tagsSuggestions,
                value = tagsValue,
                tags = tags,
                onAddTag = {
                    vm.addTag(it)
                    vm.tagsFieldValue.value = TextFieldValue("")
                    saveNote()
                },
                onDeleteTag = {
                    vm.deleteTag(it)
                    saveNote()
                },
                addFileVm = addFileVm,
                onValueChange = { vm.tagsFieldValue.value = it },
                existingAttachments = vm.existingAttachments.value,
                onDeleteExistingAttachment = { vm.deleteExistingAttachment(it) },
            )

            NPrimaryButton(
                onClick = saveNote(),
                loadingText = stringResource(R.string.saving),
                isLoading = vm.isLoading.value,
            ) {
                Icon(
                    Icons.Filled.Edit,
                    stringResource(R.string.saveNote)
                )
                Text(
                    text = stringResource(R.string.saveNote),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

        }
    }
}