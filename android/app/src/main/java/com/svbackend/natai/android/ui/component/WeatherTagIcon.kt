package com.svbackend.natai.android.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.svbackend.natai.android.R
import com.svbackend.natai.android.utils.gradientBackground

@Composable
fun WeatherTagIcon(modifier: Modifier, score: Int?, isSelected: Boolean = false) {
    val iconsMap = remember {
        mapOf(
            10 to R.drawable.weather_10,
            9 to R.drawable.weather_9,
            8 to R.drawable.weather_8,
            7 to R.drawable.weather_7,
            6 to R.drawable.weather_6,
            5 to R.drawable.weather_5,
            4 to R.drawable.weather_4,
            3 to R.drawable.weather_3,
            2 to R.drawable.weather_2,
            1 to R.drawable.weather,
            0 to R.drawable.weather,
        )
    }

    val id = iconsMap[score] ?: R.drawable.ic_care


    val m = if (isSelected) {
        modifier
            .clip(CircleShape)
            .gradientBackground(
                listOf(
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.colorScheme.secondary,
                ), angle = 45f
            )
    } else {
        modifier
    }

    Image(
        painter = painterResource(id = id),
        modifier = m.size(96.dp),
        contentDescription = null,
    )
}