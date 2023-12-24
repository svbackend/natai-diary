package com.svbackend.natai.android.ui.screen.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svbackend.natai.android.R
import com.svbackend.natai.android.entity.User
import com.svbackend.natai.android.ui.NCheckbox
import com.svbackend.natai.android.ui.NPrimaryButton
import com.svbackend.natai.android.ui.NTextField
import com.svbackend.natai.android.ui.component.CityAutocomplete
import com.svbackend.natai.android.ui.screen.LoadingScreen
import com.svbackend.natai.android.viewmodel.ManageAccountViewModel
import com.svbackend.natai.android.viewmodel.NoteViewModel
import com.svbackend.natai.android.viewmodel.UpdateProfileViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun ManageAccountScreen(
    vm: NoteViewModel,
    manageAccountViewModel: ManageAccountViewModel = viewModel(),
    onClickCreateAccount: () -> Unit,
    onClickLogin: () -> Unit,
    onClickDeleteAccount: () -> Unit,
    onLogout: () -> Unit,
) {
    val user = vm.userState

    if (user == null) {
        NotAuthorized(
            onClickCreateAccount = onClickCreateAccount,
            onClickLogin = onClickLogin
        )
        return
    } else {
        Authorized(
            user = user,
            manageAccountViewModel = manageAccountViewModel,
            vm = vm,
            onLogout = onLogout,
            onClickDeleteAccount = onClickDeleteAccount,
        )
    }
}

@Composable
fun Authorized(
    user: User,
    manageAccountViewModel: ManageAccountViewModel,
    vm: NoteViewModel,
    onLogout: () -> Unit,
    onClickDeleteAccount: () -> Unit,
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

            if (!user.isEmailVerified) {
                EmailVerificationUi(manageAccountViewModel, checkEmailVerification)
            }

            UpdateProfileUi()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(id = R.string.logout),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable {
                            scope.launch {
                                vm.logout(onLogout)
                            }
                        }
                        .padding(4.dp),
                )

                Text(
                    text = stringResource(id = R.string.deleteAccount),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable {
                            onClickDeleteAccount()
                        }
                        .padding(4.dp),
                )
            }
        }
    }
}

@Composable
fun UpdateProfileUi(
    vm: UpdateProfileViewModel = viewModel(),
) {
    if (vm.isLoading()) {
        LoadingScreen()
    } else {
        UpdateProfileForm(vm)
    }
}

@Composable
fun UpdateProfileForm(vm: UpdateProfileViewModel) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.updateProfile),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        )

        if (vm.showSuccess.value) {
            Text(
                text = stringResource(id = R.string.profileUpdated),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            )
        }

        if (vm.showError.value) {
            Text(
                text = stringResource(id = R.string.errorUpdatingProfile),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            )
        }

        NTextField(
            value = vm.name.value,
            label = stringResource(R.string.name),
            onChange = {
                vm.onChangeName(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        )

        NCheckbox(
            label = { Text(stringResource(R.string.enableEmailNotifications)) },
            value = vm.enableEmailNotifications.value
        ) {
            vm.onChangeEnableEmailNotifications()
        }

        CityAutocomplete(
            input = vm.cityInput.value,
            selectedCityId = vm.selectedCity.value,
            onCitySelected = { vm.onChangeCity(it) },
            cities = vm.cities.value,
            onInputChanged = { vm.onCityInputChange(it) },
            isLoadingAutocomplete = vm.isLoadingAutocomplete.value,
            expanded = vm.isExpanded.value,
            onToggleListVisibility = { vm.onToggleListVisibility() },
        )

        NPrimaryButton(
            onClick = { vm.updateProfile() },
            isLoading = vm.isSaving.value,
            modifier = Modifier
                .padding(bottom = 16.dp),
        ) {
            Icon(
                Icons.Filled.AccountCircle,
                stringResource(R.string.updateProfile)
            )
            Text(
                text = stringResource(R.string.updateProfile),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun EmailVerificationUi(
    manageAccountViewModel: ManageAccountViewModel,
    checkEmailVerification: () -> Job
) {
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
        modifier = Modifier
            .padding(bottom = 16.dp),
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

@Composable
fun NotAuthorized(
    onClickCreateAccount: () -> Unit,
    onClickLogin: () -> Unit,
) {
    val state = rememberScrollState()
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .verticalScroll(state)
        ) {


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier
                        .padding(top = 24.dp),
                    onClick = onClickLogin,
                ) {
                    Text(
                        text = stringResource(R.string.login),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Text(
                text = stringResource(R.string.dontHaveAccount),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                textAlign = TextAlign.Center,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier
                        .padding(top = 12.dp),
                    onClick = onClickCreateAccount,
                ) {
                    Text(
                        text = stringResource(R.string.createAccount),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            AccountBenefits()
        }
    }
}


@Composable
fun AccountBenefits() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {

        Text(
            text = stringResource(R.string.accountBenefitsTitle),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        )

        Text(
            text = stringResource(R.string.accountBenefits),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        )

        BenefitCard(
            R.drawable.baseline_assistant_24,
            "AI Therapy",
            "Receive personalized recommendations based on your notes"
        )
        BenefitCard(
            R.drawable.baseline_cloud_done_24,
            "Cloud Sync",
            "Automatically sync your notes and files across all your devices"
        )
        BenefitCard(
            R.drawable.baseline_computer_24,
            "Web Version",
            "Access your notes on the web at https://natai.app from your desktop"
        )
        BenefitCard(
            R.drawable.twotone_wb_sunny_24,
            "Weather Information",
            "Automatically attach weather information to your notes"
        )
    }
}

@Composable
fun BenefitCard(icon: Int, title: String, description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Row(Modifier.padding(16.dp)) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}