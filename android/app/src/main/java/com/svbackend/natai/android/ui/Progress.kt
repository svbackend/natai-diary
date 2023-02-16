package com.svbackend.natai.android.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NProgressBtn() {
    CircularProgressIndicator(
        color = MaterialTheme.colorScheme.onPrimary,
        strokeWidth = 1.dp,
        modifier = Modifier.size(14.dp)
    )
}