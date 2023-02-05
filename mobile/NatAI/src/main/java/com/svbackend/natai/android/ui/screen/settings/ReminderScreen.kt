package com.svbackend.natai.android.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svbackend.natai.android.R
import com.svbackend.natai.android.viewmodel.ReminderViewModel

@Composable
fun ReminderScreen(
    viewModel: ReminderViewModel = viewModel(),
    onAskForNotificationPermission: () -> Unit
) {
    val initialIsReminderEnabled = viewModel.isReminderEnabled()
    val isReminderEnabled: Boolean =
        viewModel.isReminderEnabledState.collectAsState(initial = initialIsReminderEnabled).value

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
        Text(stringResource(id = R.string.settingsReminderToggle), )
    }
}
