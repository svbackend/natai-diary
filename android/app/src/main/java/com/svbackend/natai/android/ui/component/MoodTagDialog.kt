package com.svbackend.natai.android.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MoodTagDialog(selectedScore: Int?, onChange: (Int) -> Unit) {

    @Composable
    fun MoodTagEl(m: Modifier, score: Int) {
        MoodTagIcon(
            modifier = m
                .clickable { onChange(score) }, score = score, isSelected = (selectedScore == score)
        )
    }

    Column {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            MoodTagEl(Modifier.weight(1f), 10)
            MoodTagEl(Modifier.weight(1f), 9)
            MoodTagEl(Modifier.weight(1f), 8)
        }

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            MoodTagEl(Modifier.weight(1f), 7)
            MoodTagEl(Modifier.weight(1f), 6)
            MoodTagEl(Modifier.weight(1f), 5)
        }

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            MoodTagEl(Modifier.weight(1f), 4)
            MoodTagEl(Modifier.weight(1f), 3)
            MoodTagEl(Modifier.weight(1f), 2)
        }
    }
}