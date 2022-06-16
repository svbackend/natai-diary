package com.svbackend.natai.android.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
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
import com.svbackend.natai.android.viewmodel.ThemesViewModel

@Composable
fun ThemesScreen(
    onThemeChanged: (UserTheme) -> Unit,
    themesViewModel: ThemesViewModel = viewModel()
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
                text = stringResource(R.string.settingsThemesTitle),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
            )

            Themes(
                onThemeChanged = onThemeChanged,
                vm  = themesViewModel
            )
        }
    }
}

@Composable
fun Themes(
    onThemeChanged: (UserTheme) -> Unit,
    vm: ThemesViewModel
) {
    ThemeItem(
        title = "Default",
        onClick = onThemeChanged,
        theme = UserTheme.Default,
        vm = vm,
    )
    ThemeItem(
        title = "Dynamic",
        onClick = onThemeChanged,
        theme = UserTheme.Dynamic,
        vm = vm,
    )
    ThemeItem(
        title = "Light Pink",
        onClick = onThemeChanged,
        theme = UserTheme.Pink,
        vm = vm,
    )
}

@Composable
fun ThemeItem(
    title: String,
    onClick: (UserTheme) -> Unit,
    theme: UserTheme,
    vm: ThemesViewModel,
) {
    Row(modifier = Modifier.clickable {
        vm.changeTheme(theme)
        onClick(theme)
    }) {
        Icon(
            imageVector = Icons.Filled.ArrowForward,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            contentDescription = "Apply $title theme",
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
    }
}
