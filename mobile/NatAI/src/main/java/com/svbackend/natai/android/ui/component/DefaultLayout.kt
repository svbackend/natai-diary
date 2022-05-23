package com.svbackend.natai.android.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.svbackend.natai.android.viewmodel.NoteViewModel
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultLayout(
    vm: NoteViewModel,
    drawerState: DrawerState,
    scope: CoroutineScope,
    toggleDrawer: () -> Unit,
    addNote: () -> Unit,
    onLogin: () -> Unit,
    onNavigateTo: (String) -> Unit,
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
            NavDrawer(onLogin = onLogin, vm = vm, onClick = { onNavigateTo(it) })
        },
    )
}
