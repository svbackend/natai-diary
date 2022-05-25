package com.svbackend.natai.android.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.svbackend.natai.android.utils.LocalDateTimeFormatter
import com.svbackend.natai.android.viewmodel.NoteViewModel

@Composable
fun NoteDetailsScreen(
    vm: NoteViewModel,
    noteId: String, // here just to have an example of arguments
) {
    val note = vm.selectedNote.collectAsState(initial = null).value

    if (note == null) {
        LoadingScreen()
        return
    }

    Column(
        Modifier.padding(16.dp)
    ) {
        SelectionContainer {
            Text(
                text = note.title,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            )
        }

        Text(
            text = LocalDateTimeFormatter.fullDateTime.format(note.createdAt),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        )

        SelectionContainer {
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            )
        }
    }
}