package com.svbackend.natai.android.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun NTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    label: String,
    onChange: (TextFieldValue) -> Unit,
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    TextField(
        value = value,
        label = @Composable { Text(label) },
        onValueChange = {
            onChange(it)
        },
        singleLine = singleLine,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        maxLines = maxLines
    )
}

@Composable
fun NTextarea(
    value: TextFieldValue,
    label: String,
    onChange: (TextFieldValue) -> Unit
) {
    NTextField(
        value = value,
        label = label,
        onChange = onChange,
        singleLine = false,
        maxLines = 6,
        modifier = Modifier.height(200.dp)
    )
}