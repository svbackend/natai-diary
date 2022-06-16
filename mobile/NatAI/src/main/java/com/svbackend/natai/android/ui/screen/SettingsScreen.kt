package com.svbackend.natai.android.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onThemeClick: () -> Unit,
) {
    Column(
        Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SettingItem(
            title = "Themes / Appearance",
            onClick = onThemeClick
        )
    }
}

@Composable
fun SettingItem(title: String, onClick: () -> Unit) {
    Row {
        Text(title)
        Spacer(modifier = Modifier.padding(16.dp))
        IconButton(
            onClick = onClick,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = "Open $title",
            )
        }
    }
}
