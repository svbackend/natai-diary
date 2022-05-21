package com.svbackend.natai.android.ui.screen

import android.widget.Space
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svbackend.natai.android.R
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.ui.HorizontalDivider
import com.svbackend.natai.android.utils.gradientBackground
import com.svbackend.natai.android.viewmodel.NoteViewModel
import java.text.SimpleDateFormat


@Composable
fun MainScreen(vm: NoteViewModel, onAddClick: () -> Unit, onLoginClick: () -> Unit) {
    val notes = vm.notesState
    val isLoggedIn by vm.isLoggedIn.collectAsState(initial = false)

    if (notes.isNotEmpty()) {
        LazyColumn {
            items(notes) { note ->
                NoteCard(note)
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
fun NoteCard(note: Note) {
    // card with date and title
    Surface(color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = SimpleDateFormat("dd").format(note.createdAt),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = SimpleDateFormat("MMM").format(note.createdAt),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = note.title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}