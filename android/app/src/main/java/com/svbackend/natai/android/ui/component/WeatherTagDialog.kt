package com.svbackend.natai.android.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun WeatherTagDialog(selectedScore: Int?, onChange: (Int) -> Unit) {
    @Composable
    fun WeatherTagEl(m: Modifier, score: Int) {
        WeatherTagIcon(
            modifier = m
                .clickable { onChange(score) }, score = score, isSelected = (selectedScore == score)
        )
    }

    Column {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            WeatherTagEl(Modifier.weight(1f), 10)
            WeatherTagEl(Modifier.weight(1f), 9)
            WeatherTagEl(Modifier.weight(1f), 8)
        }

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            WeatherTagEl(Modifier.weight(1f), 7)
            WeatherTagEl(Modifier.weight(1f), 6)
            WeatherTagEl(Modifier.weight(1f), 5)
        }

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            WeatherTagEl(Modifier.weight(1f), 4)
            WeatherTagEl(Modifier.weight(1f), 3)
            WeatherTagEl(Modifier.weight(1f), 2)
        }
    }
}