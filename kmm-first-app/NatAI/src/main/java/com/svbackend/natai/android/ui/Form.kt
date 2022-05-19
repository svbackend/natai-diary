package com.svbackend.natai.android.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun NTextField(
    label: String,
    initialValue: String = "",
    onChange: (newValue: String) -> Unit = { },
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    var text by remember { mutableStateOf(TextFieldValue(initialValue)) }

    TextField(
        value = text,
        label = @Composable { Text(label) },
        onValueChange = {
            text = it
            onChange(it.text)
        },
        singleLine = singleLine,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        maxLines = maxLines
    )
}

@Composable
fun NTextarea(
    label: String,
    initialValue: String = "",
    onChange: (newValue: String) -> Unit = { }
) {
    NTextField(
        label = label,
        initialValue = initialValue,
        onChange = onChange,
        singleLine = false,
        maxLines = 6
    )
}