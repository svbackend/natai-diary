package com.svbackend.natai.android.ui.screen.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svbackend.natai.android.R
import com.svbackend.natai.android.entity.User
import com.svbackend.natai.android.ui.NPrimaryButton
import com.svbackend.natai.android.viewmodel.ManageAccountViewModel
import com.svbackend.natai.android.viewmodel.NoteViewModel
import kotlinx.coroutines.launch

@Composable
fun ManageAccountScreen(
    vm: NoteViewModel,
    manageAccountViewModel: ManageAccountViewModel = viewModel(),
    onClickCreateAccount: () -> Unit,
    onLogout: () -> Unit,
) {
    val user = vm.userState

    if (user == null) {
        NotAuthorized {
            onClickCreateAccount()
        }
        return
    } else {
        Authorized(
            user = user,
            manageAccountViewModel = manageAccountViewModel,
            vm = vm,
            onLogout = onLogout,
        )
    }
}

@Composable
fun Authorized(
    user: User,
    manageAccountViewModel: ManageAccountViewModel,
    vm: NoteViewModel,
    onLogout: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val checkEmailVerification = {
        scope.launch {
            manageAccountViewModel.checkEmailVerification()
        }
    }

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

            Text(
                text = user.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            )

            if (!user.isEmailVerified) {
                Text(
                    text = stringResource(id = R.string.emailNotVerified),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                )

                if (manageAccountViewModel.showStillNotVerifiedError.value) {
                    Text(
                        text = stringResource(id = R.string.yourEmailStillNotVerified),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                    )
                }

                NPrimaryButton(
                    onClick = { checkEmailVerification() },
                    isLoading = manageAccountViewModel.userQuery.isLoading.value,
                ) {
                    Icon(
                        Icons.Filled.Email,
                        stringResource(R.string.done)
                    )
                    Text(
                        text = stringResource(R.string.done),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Text(
                text = stringResource(id = R.string.logout),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        scope.launch {
                            vm.logout(onLogout)
                        }
                    }
                    .padding(top = 16.dp),
            )

            // todo add logout button?
            // delete account button?
        }
    }
}

@Composable
fun NotAuthorized(onClickCreateAccount: () -> Unit) {
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
    }
}


