package com.svbackend.natai.android.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.ui.HorizontalDivider
import com.svbackend.natai.android.viewmodel.NoteViewModel
import java.text.SimpleDateFormat


@Composable
fun MainScreen(vm: NoteViewModel = viewModel()) {
    val notes by vm.notes.collectAsState(initial = emptyList())

    LazyColumn {
        items(notes) { note ->
            NoteCard(note)
            HorizontalDivider()
        }
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