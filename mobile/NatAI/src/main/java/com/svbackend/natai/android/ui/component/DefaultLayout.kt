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
    val isSync by vm.isSyncing.collectAsState(initial = false)

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
        },
        drawerContent = {
            NavDrawer(onLogin = onLogin, vm = vm, onClick = { onNavigateTo(it) })
        },
    )
}
