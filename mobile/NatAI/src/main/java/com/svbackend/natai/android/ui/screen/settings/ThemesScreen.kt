package com.svbackend.natai.android.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svbackend.natai.android.ui.UserTheme
import com.svbackend.natai.android.viewmodel.ThemesViewModel

@Composable
fun ThemesScreen(
    onThemeChanged: (UserTheme) -> Unit,
    themesViewModel: ThemesViewModel = viewModel()
) {
    Column {
        ThemeItem(
            title = "Default",
            ItemIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "Apply default theme",
                )
            },
            onClick = onThemeChanged,
            theme = UserTheme.Default,
            themesViewModel = themesViewModel,
        )
        ThemeItem(
            title = "Dynamic",
            ItemIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "Apply dynamic theme",
                )
            },
            onClick = onThemeChanged,
            theme = UserTheme.Dynamic,
            themesViewModel = themesViewModel,
        )
        ThemeItem(
            title = "Light Pink",
            ItemIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "Apply light pink theme",
                )
            },
            onClick = onThemeChanged,
            theme = UserTheme.Pink,
            themesViewModel = themesViewModel,
        )
    }
}

@Composable
fun ThemeItem(
    title: String,
    ItemIcon: @Composable () -> Unit,
    onClick: (UserTheme) -> Unit,
    theme: UserTheme,
    themesViewModel: ThemesViewModel,
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        ItemIcon()
        Spacer(modifier = Modifier.padding(16.dp))
        Text(text = title, modifier = Modifier.padding(16.dp))
        IconButton(onClick = {
            themesViewModel.changeTheme(theme)
            onClick(theme)
        }) {
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = "Apply $title",
            )
        }
    }
}
