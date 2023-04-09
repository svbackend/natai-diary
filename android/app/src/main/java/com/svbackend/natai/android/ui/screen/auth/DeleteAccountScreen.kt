package com.svbackend.natai.android.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svbackend.natai.android.R
import com.svbackend.natai.android.ui.NPrimaryButton
import com.svbackend.natai.android.viewmodel.DeleteAccountViewModel
import com.svbackend.natai.android.viewmodel.NoteViewModel

@Composable
fun DeleteAccountScreen(
    vm: NoteViewModel,
    deleteAccountViewModel: DeleteAccountViewModel = viewModel(),
    onAccountDeleted: () -> Unit,
) {
    val user = vm.userState

    if (user == null) {
        Text(text = "You are not authorized")
        return
    }

    val deleteAccount = {
        deleteAccountViewModel.deleteAccount {
            vm.logout {
                onAccountDeleted()
            }
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
                text = user.name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            )

            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.deleteAccountWarning),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            NPrimaryButton(
                onClick = deleteAccount,
                isLoading = deleteAccountViewModel.isLoading.value,
                modifier = Modifier
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.deleteAccount),
                )
            }

            if (deleteAccountViewModel.isError.value) {
                Text(
                    text = stringResource(id = R.string.accountDeleteError),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                )
            }
        }
    }
}