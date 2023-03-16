package com.svbackend.natai.android.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.svbackend.natai.android.R

@Composable
fun NPrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier,
    isLoading: Boolean = false,
    loadingText: String = stringResource(id = R.string.loading),
    content: @Composable RowScope.() -> Unit,
) {
    if (isLoading) {
        Button(
            onClick = {}, modifier = loadingModifier
                .fillMaxWidth()
        ) {
            NProgressBtn()
            Text(
                text = loadingText,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    } else {
        Button(
            onClick = onClick, modifier = modifier
                .fillMaxWidth()
        ) {
            content()
        }
    }
}