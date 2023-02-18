package com.svbackend.natai.android.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.svbackend.natai.android.BuildConfig

const val API_URL = BuildConfig.API_BASE_URL

@Composable
fun AppInfoScreen() {
    // show app version, api url

    Column {
        Text(text = "App version: ${BuildConfig.VERSION_NAME}")
        Text(text = "API URL: $API_URL")
    }
}