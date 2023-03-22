package com.svbackend.natai.android.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svbackend.natai.android.R
import com.svbackend.natai.android.ui.UserTheme
import com.svbackend.natai.android.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    vm: SettingsViewModel = viewModel(),
    onThemeClick: () -> Unit,
    onReminderClick: () -> Unit,
    onAppInfoClick: () -> Unit,
    onFeedbackClick: () -> Unit,
) {
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
                text = stringResource(R.string.settingsTitle),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
            )

            SettingThemesItem(
                title = stringResource(R.string.settingsThemesTitle),
                onClick = onThemeClick
            )

            SettingReminderItem(
                vm = vm,
                title = stringResource(R.string.settingsReminderTitle),
                onClick = onReminderClick,
            )

            SettingAppInfoItem(
                onClick = onAppInfoClick,
            )

            SettingFeedbackItem(
                onClick = onFeedbackClick,
            )
        }
    }
}

@Composable
fun SettingThemesItem(title: String, onClick: () -> Unit) {
    Row(modifier = Modifier.clickable { onClick() }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_preview_24),
            modifier = Modifier.padding(16.dp),
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp),
            text = title,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            textAlign = TextAlign.Start
        )

        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(16.dp),
            text = UserTheme.numberOfThemes.toString(),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun SettingReminderItem(vm: SettingsViewModel, title: String, onClick: () -> Unit) {
    val isEnabled = vm.initIsReminderEnabled()
    val time = vm.initReminderTime()

    Row(modifier = Modifier.clickable { onClick() }) {
        Icon(
            Icons.Filled.Notifications,
            contentDescription = title,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp),
            text = title,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            textAlign = TextAlign.Start
        )

        // todo add reminder time
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(16.dp),
            text = if (isEnabled) time.toString() else "(off)",
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun SettingAppInfoItem(onClick: () -> Unit) {
    Row(modifier = Modifier.clickable { onClick() }) {
        Icon(
            Icons.Filled.Info,
            contentDescription = stringResource(R.string.settingsAppInfoTitle),
            modifier = Modifier.padding(16.dp)
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp),
            text = stringResource(R.string.settingsAppInfoTitle),
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            textAlign = TextAlign.Start
        )
    }
}

// Feedback Item
@Composable
fun SettingFeedbackItem(onClick: () -> Unit) {
    Row(modifier = Modifier.clickable { onClick() }) {
        Icon(
            painter = painterResource(id = R.drawable.round_feedback_24),
            contentDescription = stringResource(R.string.settingsFeedbackTitle),
            modifier = Modifier.padding(16.dp)
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp),
            text = stringResource(R.string.settingsFeedbackTitle),
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            textAlign = TextAlign.Start
        )
    }
}
