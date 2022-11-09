package com.svbackend.natai.android.ui.screen.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svbackend.natai.android.R
import com.svbackend.natai.android.http.exception.RegistrationErrorException
import com.svbackend.natai.android.ui.NCheckbox
import com.svbackend.natai.android.ui.NPasswordField
import com.svbackend.natai.android.ui.NPrimaryButton
import com.svbackend.natai.android.ui.NTextField
import com.svbackend.natai.android.viewmodel.RegistrationViewModel
import kotlinx.coroutines.launch

@Composable
fun RegistrationScreen(
    vm: RegistrationViewModel = viewModel(),
    onRegistrationSuccess: () -> Unit,
    onClickLogin: () -> Unit,
    onClickTerms: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    fun onRegister(): () -> Unit {
        if (!vm.acceptTerms.value) {
            return {
                Toast
                    .makeText(context, "Please accept terms and conditions", Toast.LENGTH_LONG)
                    .show()
            }
        }

        if (vm.name.value.text.isEmpty() || vm.email.value.text.isEmpty() || vm.password.value.text.isEmpty()) {
            return {
                Toast
                    .makeText(context, "Email, name and password are required", Toast.LENGTH_LONG)
                    .show()
            }
        }

        return {
            vm.isLoading.value = true
            scope.launch {

                try {
                    vm.register()
                    onRegistrationSuccess()
                } catch (e: RegistrationErrorException) {
                    Toast
                        .makeText(context, e.message, Toast.LENGTH_SHORT)
                        .show()
                }

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
                text = stringResource(R.string.registration),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            )

            NTextField(
                value = vm.email.value,
                label = stringResource(R.string.email),
                onChange = {
                    vm.email.value = it
                }
            )

            NTextField(
                value = vm.name.value,
                label = stringResource(R.string.name),
                onChange = {
                    vm.name.value = it
                }
            )

            NPasswordField(
                value = vm.password.value,
                label = stringResource(R.string.password),
                onChange = {
                    vm.password.value = it
                }
            )

            // accept terms & conditions checkbox
            NCheckbox(
                label = {
                    Row(Modifier.padding(start = 4.dp)) {
                        Text(
                            text = stringResource(R.string.accept),
                        )
                        Text(" ")
                        Text(
                            text = stringResource(R.string.termsAndConditions),
                            modifier = Modifier
                                .clickable {
                                    onClickTerms()
                                },
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                value = vm.acceptTerms.value,
                onToggle = {
                    vm.acceptTerms.value = !vm.acceptTerms.value
                },
            )

            NPrimaryButton(
                onClick = onRegister(),
                isLoading = vm.isLoading.value,
            ) {
                Icon(
                    Icons.Filled.Person,
                    stringResource(R.string.register)
                )
                Text(
                    text = stringResource(R.string.register),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            LoginLink {
                onClickLogin()
            }
        }

    }
}

@Composable
fun LoginLink(onClick: () -> Unit) {
    // text centered
    Text(
        text = stringResource(R.string.alreadyHaveAccount),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        textAlign = TextAlign.Center,
    )

    Text(
        text = stringResource(R.string.login),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clickable { onClick() },
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.primary,
    )
}