package com.svbackend.natai.android.ui.screen

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.svbackend.natai.android.R
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.utils.LocalDateTimeFormatter
import com.svbackend.natai.android.viewmodel.NoteViewModel

@Composable
fun NoteDetailsScreen(
    vm: NoteViewModel,
    noteId: String, // here just to have an example of arguments
    onEditClick: (String) -> Unit,
    onDeleteClick: (LocalNote) -> Unit,
) {
    val note = vm.selectedNote.collectAsState(initial = null).value

    val context = LocalContext.current

    if (note == null) {
        LoadingScreen()
        return
    }

    Column(
        Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
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
            text = LocalDateTimeFormatter.fullDate.format(note.actualDate),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        )

        SelectionContainer {
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            )
        }

        // Space between
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        ) {
            ExtendedFloatingActionButton(
                text = {
                    Text(
                        text = stringResource(R.string.editNote),
                    )
                },
                icon = {
                    Icon(
                        Icons.Filled.Edit,
                        stringResource(R.string.editNote)
                    )
                },
                onClick = { onEditClick(noteId) }
            )


            ExtendedFloatingActionButton(
                text = {
                    Text(
                        text = stringResource(R.string.deleteNote),
                    )
                },
                icon = {
                    Icon(
                        Icons.Filled.Delete,
                        stringResource(R.string.deleteNote)
                    )
                },
                onClick = {
                    Toast
                        .makeText(context, "Note deleted!", Toast.LENGTH_SHORT)
                        .show()
                    onDeleteClick(note)
                }
            )
        }
    }
}