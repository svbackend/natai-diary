package com.svbackend.natai.android.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.svbackend.natai.android.R
import com.svbackend.natai.android.ui.UserTheme

@Composable
fun SettingsScreen(
    onThemeClick: () -> Unit,
    onReminderClick: () -> Unit,
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

            SettingItem(
                title = stringResource(R.string.settingsThemesTitle),
                onClick = onThemeClick
            )

            SettingItem(
                title = stringResource(R.string.settingsReminderTitle),
                onClick = onReminderClick
            )
        }
    }
}

@Composable
fun SettingItem(title: String, onClick: () -> Unit) {
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
