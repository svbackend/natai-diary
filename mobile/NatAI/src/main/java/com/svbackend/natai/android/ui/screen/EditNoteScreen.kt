package com.svbackend.natai.android.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.svbackend.natai.android.R
import com.svbackend.natai.android.ui.*
import com.svbackend.natai.android.ui.component.TagsField
import com.svbackend.natai.android.viewmodel.EditNoteViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun EditNoteScreen(
    vm: EditNoteViewModel,
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
                vm.saveNote(note = note)
                onSuccess()
            }
        }
    }

    val actualDate = vm.actualDate.value
    val onDateChange = { date: LocalDate ->
        vm.actualDateChanged(date)
    }
    val tags = vm.tags.value
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
                }
            )
            NTextarea(
                value = vm.content.value,
                label = stringResource(R.string.noteContent),
                onChange = {
                    vm.content.value = it
                }
            )
            TagsField(
                context = context,
                value = tagsValue,
                tags = tags,
                onAddTag = {
                    vm.addTag(it)
                    vm.tagsFieldValue.value = TextFieldValue("")
                },
                onDeleteTag = { vm.deleteTag(it) },
                onValueChange = { vm.tagsFieldValue.value = it },
                tagsSuggestions = tagsSuggestions,
            )
            if (vm.isLoading.value) {
                Button(onClick = {}, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    NProgressBtn()
                    Text(
                        text = stringResource(R.string.saving),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            } else {
                Button(onClick = saveNote(), modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
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
}