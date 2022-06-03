package com.svbackend.natai.android.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.svbackend.natai.android.R
import com.svbackend.natai.android.ui.NTextField
import com.svbackend.natai.android.ui.NTextarea
import com.svbackend.natai.android.viewmodel.EditNoteViewModel
import kotlinx.coroutines.launch

@Composable
fun EditNoteScreen(
    vm: EditNoteViewModel,
    onSuccess: () -> Unit
) {
    val note = vm.note.collectAsState(initial = null).value

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
                vm.saveNote(
                    note = note,
                    newTitle = vm.title.value.text,
                    newContent = vm.content.value.text
                )
                onSuccess()
            }
        }
    }

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
        if (vm.isLoading.value) {
            ExtendedFloatingActionButton(
                text = {
                    Text(
                        text = stringResource(R.string.saving),
                    )
                },
                icon = {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 1.dp,
                        modifier = Modifier.size(14.dp)
                    )
                },
                onClick = {}
            )
        } else {
            ExtendedFloatingActionButton(
                text = {
                    Text(
                        text = stringResource(R.string.saveNote),
                    )
                },
                icon = {
                    Icon(
                        Icons.Filled.Edit,
                        stringResource(R.string.saveNote)
                    )
                },
                onClick = saveNote()
            )

        }

    }
}