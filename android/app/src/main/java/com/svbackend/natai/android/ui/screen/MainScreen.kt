package com.svbackend.natai.android.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.svbackend.natai.android.R
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.ui.HorizontalDivider
import com.svbackend.natai.android.ui.VerticalDivider
import com.svbackend.natai.android.ui.component.AttachmentsPreview
import com.svbackend.natai.android.ui.component.RegularTagsRow
import com.svbackend.natai.android.ui.component.SpecialTagsRow
import com.svbackend.natai.android.utils.LocalDateTimeFormatter
import com.svbackend.natai.android.utils.gradientBackground
import com.svbackend.natai.android.utils.throttleLatest
import com.svbackend.natai.android.viewmodel.NoteViewModel
import java.time.LocalDate

data class NotesGroup(
    val date: LocalDate,
    val notes: MutableList<LocalNote>
)

@Composable
fun MainScreen(
    vm: NoteViewModel,
    onAddClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onNoteClick: (LocalNote) -> Unit
) {
    val notes = vm.notesState
    val currentUser = vm.userState
    val isLoggedIn = currentUser != null
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val pullToRefreshCallback = throttleLatest<Offset>(
        intervalMs = 500L,
        coroutineScope = scope,
        destinationFunction = { dragAmount ->
            val isPullingDown = dragAmount.y > 0

            if (isPullingDown && isLoggedIn) {
                // show short toast message that sync is started
                Toast
                    .makeText(context, "Synchronization started..", Toast.LENGTH_SHORT)
                    .show()

                vm.resetLastSyncTime()
                vm.sync()
            }
        }
    )

    val pullToRefreshModifier = Modifier.pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
            change.consume()

            pullToRefreshCallback(dragAmount)
        }
    }

    if (notes.isNotEmpty()) {
        val groups = mapNotes(notes)
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = pullToRefreshModifier.fillMaxWidth(),
            content = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Wallpaper()
                }
            }
        )

        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 240.dp),
        ) {
            LazyColumn {
                items(
                    items = groups,
                    key = { group -> group.date.toString() }
                ) { group ->
                    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
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
                        VerticalDivider(
                            thickness = 4.dp,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .clip(CircleShape),
                        )
                        Column {
                            group.notes.reversed().forEachIndexed { i, note ->
                                NoteCard(note, onNoteClick)
                                if (i != group.notes.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier
                                            .padding(horizontal = 12.dp)
                                            .clip(CircleShape),
                                        thickness = 2.dp,
                                    )
                                }
                            }
                        }
                    }
                    HorizontalDivider()

                }
            }
        }

    } else {
        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.large,
            content = {
                Column(
                    modifier = pullToRefreshModifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Wallpaper()
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

                    if (!isLoggedIn) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row {
                            ExtendedFloatingActionButton(
                                text = {
                                    Text(
                                        text = stringResource(R.string.createAccount),
                                    )
                                },
                                icon = {
                                    Icon(
                                        Icons.Default.Person,
                                        stringResource(R.string.createAccount)
                                    )
                                },
                                onClick = onRegisterClick
                            )
                        }
                    }

                }
            }
        )


    }


}

@Composable
fun NoteCard(note: LocalNote, onNoteClick: (LocalNote) -> Unit) {
    var contentPreview = note.content.substring(0, minOf(note.content.length, 100))

    if (contentPreview.length < note.content.length) {
        contentPreview += "..."
    }

    val time = LocalDateTimeFormatter.time.format(note.createdAt)

    Surface(
        modifier = Modifier
            .clickable {
                onNoteClick(note)
            }
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth()
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
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SpecialTagsRow(tags = note.tags)
                RegularTagsRow(tags = note.tags)
                Text(
                    text = contentPreview,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                AttachmentsPreview(attachments = note.attachments)
            }
        }
    }
}

fun mapNotes(notes: List<LocalNote>): List<NotesGroup> {
    val groupedNotes = mutableMapOf<String, NotesGroup>()
    notes.forEach {
        val idx = LocalDateTimeFormatter.fullDate.format(it.actualDate)
        val notesGroup = groupedNotes[idx]
        if (notesGroup == null) {
            groupedNotes[idx] = NotesGroup(it.actualDate, mutableListOf(it))
        } else {
            notesGroup.notes.add(it)
        }
    }

    return groupedNotes.values.toList()
}

@Composable
fun Wallpaper() {
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
    ) {
        Image(
            painter = painterResource(id = R.drawable.forest2),
            contentDescription = "Forest wallpaper",
            modifier = Modifier.matchParentSize(),
        )
    }
}