package com.svbackend.natai.android.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svbackend.natai.android.R
import com.svbackend.natai.android.ui.NPrimaryButton
import com.svbackend.natai.android.viewmodel.ReminderViewModel
import kotlinx.coroutines.launch
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    viewModel: ReminderViewModel = viewModel(),
    onAskForNotificationPermission: () -> Unit,
    onSave: () -> Unit,
) {
    val isReminderEnabled: Boolean = viewModel
        .isReminderEnabledState
        .collectAsState()
        .value

    val initialTime = viewModel.initReminderTime()
    val timePickerState = rememberTimePickerState(
        initialTime.hour,
        initialTime.minute
    )

    val coroutineScope = rememberCoroutineScope()

    val onClickSave = {
        coroutineScope.launch {
            viewModel.changeReminderTime(
                LocalTime.of(
                    timePickerState.hour,
                    timePickerState.minute
                )
            )

            onSave()
        }
    }

    val isSaving = viewModel.isSaving.collectAsState().value

    viewModel.initIsReminderEnabled()

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.settingsReminderTitle),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
            )

            ReminderToggle(
                currentValue = isReminderEnabled,
                onToggle = {
                    viewModel.toggleReminder(it)

                    if (it) {
                        onAskForNotificationPermission()
                    }
                }
            )

            if (isReminderEnabled) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TimePicker(
                        state = timePickerState,
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    NPrimaryButton(
                        onClick = {
                            onClickSave()
                        },
                        loadingText = stringResource(R.string.saving),
                        isLoading = isSaving,
                    ) {
                        Icon(
                            Icons.Filled.Notifications,
                            stringResource(R.string.save)
                        )
                        Text(
                            text = stringResource(R.string.save),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReminderToggle(currentValue: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .toggleable(
                role = Role.Switch,
                value = currentValue,
                onValueChange = { onToggle(it) },
            )
    ) {
        Switch(
            checked = currentValue,
            onCheckedChange = null
        )
        Spacer(Modifier.width(8.dp))
        Text(stringResource(id = R.string.settingsReminderToggle))
    }
}