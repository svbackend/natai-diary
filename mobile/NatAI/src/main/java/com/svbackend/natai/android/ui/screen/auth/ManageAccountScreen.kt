package com.svbackend.natai.android.ui.screen.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svbackend.natai.android.R
import com.svbackend.natai.android.entity.User
import com.svbackend.natai.android.viewmodel.NoteViewModel

@Composable
fun ManageAccountScreen(
    vm: NoteViewModel = viewModel(),
    onClickCreateAccount: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val user = vm.user.collectAsState(initial = null).value

    println("ManageAccountScreen USER")
    println(user)

    if (user == null) {
        NotAuthorized {
            onClickCreateAccount()
        }
        return
    } else {
        Authorized(user)
    }
}

@Composable
fun Authorized(user: User) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            Modifier
                .padding(16.dp)
        ) {
            Text(
                text = user.email,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            )
        }

    }
}

@Composable
fun NotAuthorized(onClickCreateAccount: () -> Unit) {
    // text centered
    Text(
        text = stringResource(R.string.dontHaveAccount),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        textAlign = TextAlign.Center,
    )

    Text(
        text = stringResource(R.string.createAccount),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clickable { onClickCreateAccount() },
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.primary,
    )
}
