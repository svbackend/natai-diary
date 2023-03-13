package com.svbackend.natai.android.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svbackend.natai.android.R
import com.svbackend.natai.android.ui.AppDateRow
import com.svbackend.natai.android.ui.NPrimaryButton
import com.svbackend.natai.android.ui.NTextField
import com.svbackend.natai.android.ui.NTextarea
import com.svbackend.natai.android.ui.component.TagsAndFilesRow
import com.svbackend.natai.android.utils.throttleLatest
import com.svbackend.natai.android.viewmodel.AddFileViewModel
import com.svbackend.natai.android.viewmodel.NewNoteViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun NewNoteScreen(
    vm: NewNoteViewModel = viewModel(),
    addFileVm: AddFileViewModel = viewModel(),
    onSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val tagsSuggestions by vm.tagsSuggestions.collectAsState(initial = emptyList())
    val emptyNoteError = stringResource(R.string.contentOrTagsRequired)

    val onTitleChange: (String) -> Unit = throttleLatest(
        intervalMs = 350L,
        coroutineScope = scope,
        destinationFunction = vm::saveTitle
    )

    val onContentChange: (String) -> Unit = throttleLatest(
        intervalMs = 350L,
        coroutineScope = scope,
        destinationFunction = vm::saveContent
    )

    val actualDate = vm.actualDate.value
    val onDateChange = { date: LocalDate ->
        vm.actualDateChanged(date)
    }

    fun addNote(): () -> Unit {
        val title = vm.title.value.text
        val content = vm.content.value.text
        val tagsCount = vm.tags.value.count()
        val filesCount = addFileVm.addedFiles.value.count()

        if (title.isEmpty() && content.isEmpty() && tagsCount == 0 && filesCount == 0) {
            return {
                Toast
                    .makeText(context, emptyNoteError, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return {
            scope.launch {
                val addedFiles = addFileVm.processAddedFiles()
                vm.addNote(addedFiles)
                onSuccess()
            }
        }

    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = stringResource(R.string.newNote),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
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
                    onTitleChange(it.text)
                }
            )
            NTextarea(
                value = vm.content.value,
                label = stringResource(R.string.noteContent),
                onChange = {
                    vm.content.value = it
                    onContentChange(it.text)
                }
            )

            val tags by vm.tags
            val tagsValue = vm.tagsFieldValue.value

            TagsAndFilesRow(
                tagsSuggestions = tagsSuggestions,
                value = tagsValue,
                tags = tags,
                onAddTag = {
                    vm.addTag(it)
                    vm.tagsFieldValue.value = TextFieldValue("")
                },
                onDeleteTag = {
                    vm.deleteTag(it)
                },
                addFileVm = addFileVm,
                onValueChange = { vm.tagsFieldValue.value = it },
            )

            NPrimaryButton(
                onClick = addNote(),
                loadingText = stringResource(R.string.saving),
                isLoading = vm.isLoading.value,
            ) {
                Icon(
                    Icons.Filled.Add,
                    stringResource(R.string.addNote)
                )
                Text(
                    text = stringResource(R.string.addNote),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

        }
    }
}