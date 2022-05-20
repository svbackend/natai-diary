package com.svbackend.natai.android.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.svbackend.natai.android.R
import com.svbackend.natai.android.viewmodel.NoteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultLayout(
    vm: NoteViewModel,
    drawerState: DrawerState,
    scope: CoroutineScope,
    toggleDrawer: () -> Unit,
    addNote: () -> Unit,
    onLogin: () -> Unit,
    content: @Composable() () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        content = {
            Scaffold(
                bottomBar = {
                    BottomBar(
                        toggleDrawer = toggleDrawer,
                        addNote = addNote
                    )
                },
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
        },
        drawerContent = {
            NavDrawer(onLogin = onLogin, vm = vm)
        },
    )
}
