package com.svbackend.natai.android.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.svbackend.natai.android.viewmodel.NoteViewModel


@Composable
fun DefaultLayout(
    vm: NoteViewModel,
    addNote: () -> Unit,
    onNavigateTo: (String) -> Unit,
    content: @Composable() () -> Unit
) {
    val isSync by vm.isSyncing.collectAsState(initial = false)

    Scaffold(
        bottomBar = {
            BottomBar(
                onNavigateTo = onNavigateTo,
                addNote = addNote
            )
        },
        topBar = {
            if (isSync) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .height(3.dp)
                        .fillMaxWidth()
                )
            }
        }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = it.calculateBottomPadding()),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}
