package com.svbackend.natai.android.ui.component

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BottomBar(
    toggleDrawer: () -> Unit,
    addNote: () -> Unit
) {
    BottomAppBar(
        icons = {
            IconButton(
                onClick = { toggleDrawer() },
                modifier = Modifier.fillMaxHeight(),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "Open/Close Menu",
                )
            }
            IconButton(
                onClick = {},
                modifier = Modifier.fillMaxHeight(),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "Search",
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { addNote() },
                elevation = BottomAppBarDefaults.floatingActionButtonElevation()
            ) {
                Icon(Icons.Filled.Add, "Add Note")
            }
        }
    )
}