package com.svbackend.natai.android.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.svbackend.natai.android.R
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.ui.HorizontalDivider
import com.svbackend.natai.android.ui.VerticalDivider
import com.svbackend.natai.android.utils.LocalDateTimeFormatter
import com.svbackend.natai.android.utils.gradientBackground
import com.svbackend.natai.android.viewmodel.NoteViewModel
import java.time.Instant

data class NotesGroup(
    val date: Instant,
    val notes: MutableList<Note>
)

@Composable
fun MainScreen(
    vm: NoteViewModel,
    onAddClick: () -> Unit,
    onLoginClick: () -> Unit,
    onNoteClick: (Note) -> Unit
) {
    val notes = vm.notesState
    val isLoggedIn by vm.isLoggedIn.collectAsState(initial = false)

    if (notes.isNotEmpty()) {
        val groups = mapNotes(notes)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(groups) { group ->
                Row {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = LocalDateTimeFormatter.day.format(group.date),
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 5.dp),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = LocalDateTimeFormatter.monthShortName.format(group.date),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 2.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = LocalDateTimeFormatter.year.format(group.date),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    VerticalDivider()
                    Column {
                        group.notes.forEach { note ->
                            NoteCard(note, onNoteClick)
                        }
                    }
                }
                HorizontalDivider()

            }
        }
    } else {
        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.large,
            content = {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .size(200.dp)
                            .gradientBackground(
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.secondary,
                                ), angle = 45f
                            )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.noNotesTitle),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.noNotesText),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row {
                        ExtendedFloatingActionButton(
                            text = {
                                Text(
                                    text = stringResource(R.string.addNote),
                                )
                            },
                            icon = {
                                Icon(
                                    Icons.Filled.Add,
                                    stringResource(R.string.addNote)
                                )
                            },
                            onClick = onAddClick
                        )
                        if (!isLoggedIn) {
                            Spacer(modifier = Modifier.width(16.dp))
                            ExtendedFloatingActionButton(
                                text = {
                                    Text(
                                        text = stringResource(R.string.login),
                                    )
                                },
                                icon = {
                                    Icon(
                                        Icons.Outlined.AccountCircle,
                                        stringResource(R.string.login)
                                    )
                                },
                                onClick = onLoginClick
                            )
                        }
                    }

                }
            }
        )


    }


}

@Composable
fun NoteCard(note: Note, onNoteClick: (Note) -> Unit) {
    var contentPreview = note.content.substring(0, minOf(note.content.length, 100))

    if (contentPreview.length < note.content.length) {
        contentPreview += "..."
    }

    val time = LocalDateTimeFormatter.time.format(note.createdAt)

    Surface(
        modifier = Modifier.clickable {
            onNoteClick(note)
        },
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(bottom = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = contentPreview,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

fun mapNotes(notes: List<Note>): List<NotesGroup> {
    val groupedNotes = mutableMapOf<String, NotesGroup>()
    notes.forEach {
        val idx = LocalDateTimeFormatter.fullDate.format(it.createdAt)
        val notesGroup = groupedNotes[idx]
        if (notesGroup == null) {
            groupedNotes[idx] = NotesGroup(it.createdAt, mutableListOf(it))
        } else {
            notesGroup.notes.add(it)
        }
    }

    return groupedNotes.values.toList()
}