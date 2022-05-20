package com.svbackend.natai.android.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svbackend.natai.android.ui.NProgressBtn
import com.svbackend.natai.android.ui.NTextField
import com.svbackend.natai.android.ui.NTextarea
import com.svbackend.natai.android.R
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.viewmodel.NewNoteViewModel
import kotlinx.coroutines.launch

@Composable
fun NewNoteScreen(
    vm: NewNoteViewModel = viewModel(),
    onSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun addNote(): () -> Unit {
        if (vm.title.value.text.isEmpty() || vm.content.value.text.isEmpty()) {
            return {
                Toast
                    .makeText(context, "Title and content are required", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return {
            vm.isLoading.value = true
            scope.launch {
                vm.repository.insert(
                    Note(
                        title = vm.title.value.text,
                        content = vm.content.value.text,
                    )
                )
            }.invokeOnCompletion {
                vm.isLoading.value = false
            }
        }

    }

    Column(
        Modifier.padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.newNote),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        )
        NTextField(
            value = vm.title.value,
            label = stringResource(R.string.noteTitle),
            onChange = { vm.title.value = it }
        )
        NTextarea(
            value = vm.content.value,
            label = stringResource(R.string.noteContent),
            onChange = { vm.content.value = it }
        )
        if (vm.isLoading.value) {
            Button(onClick = {}) {
                NProgressBtn()
                Text(
                    text = stringResource(R.string.saving),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        } else {
            Button(onClick = addNote()) {
                Text(text = stringResource(R.string.addNote))
            }
        }

    }
}