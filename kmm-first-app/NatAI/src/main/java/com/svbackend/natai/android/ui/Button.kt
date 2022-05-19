package com.svbackend.natai.android.ui

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*

@Composable
fun NButton(
    content: String,
    onClick: () -> Unit = {},
) {
    var isLoading by remember { mutableStateOf(false) }

    Button(
        onClick = {
            isLoading = true
            onClick()
            isLoading = false
        }
    ) {
        if (isLoading) {
            Text("Loading..")
        } else {
            Text(content)
        }
    }
}