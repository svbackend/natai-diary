package com.svbackend.natai.android.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.svbackend.natai.android.R
import com.svbackend.natai.android.coil.CropTransformation
import com.svbackend.natai.android.entity.ExistingAttachmentDto
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.ui.component.AllTagsBadges
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
    val attachments = vm.selectedNoteAttachments.value

    val context = LocalContext.current

    if (note == null) {
        LoadingScreen()
        return
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
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

            AllTagsBadges(
                tags = note.tags,
            )

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

            AttachmentsGrid(attachments)

            // Space between
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            ) {
                Button(
                    onClick = { onEditClick(noteId) },
                    modifier = Modifier
                        .padding(top = 16.dp)
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        stringResource(R.string.editNote)
                    )
                    Text(
                        text = stringResource(R.string.editNote),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Button(
                    onClick = {
                        Toast
                            .makeText(context, "Note deleted!", Toast.LENGTH_SHORT)
                            .show()
                        onDeleteClick(note)
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        stringResource(R.string.deleteNote)
                    )
                    Text(
                        text = stringResource(R.string.deleteNote),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AttachmentsGrid(attachments: List<ExistingAttachmentDto>) {
    if (attachments.isEmpty()) {
        return
    }

    val attachmentsRows = attachments.chunked(3)

    attachmentsRows.forEach { row ->
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        ) {
            row.forEach { attachment ->
                Column {
                    AsyncImage(
                        model = attachment.previewUri ?: attachment.uri ?: painterResource(id = R.drawable.placeholder),
                        contentDescription = attachment.filename,
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 15)),
                        error = painterResource(id = R.drawable.placeholder)
                    )
                }
            }
        }
    }
}