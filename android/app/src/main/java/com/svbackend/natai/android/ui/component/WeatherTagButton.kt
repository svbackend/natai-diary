package com.svbackend.natai.android.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.svbackend.natai.android.R
import com.svbackend.natai.android.entity.TagEntityDto

@Composable
fun WeatherTagButton(
    weatherTag: TagEntityDto?,
    onClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.Center) {
        IconButton(
            onClick = onClick,
        ) {
            if (weatherTag != null) {
                SpecialTagIcon(
                    modifier = Modifier.size(64.dp),
                    tag = weatherTag.tag,
                    score = weatherTag.score
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.weather),
                    modifier = Modifier.size(64.dp),
                    contentDescription = "Add weather tag"
                )
            }
        }
        Text(
            text = "#weather",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}