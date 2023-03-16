package com.svbackend.natai.android.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
fun MoodTagButton(
    moodTag: TagEntityDto?,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        IconButton(
            onClick = onClick,
        ) {
            if (moodTag != null) {
                SpecialTagIcon(
                    modifier = Modifier.size(64.dp),
                    tag = moodTag.tag,
                    score = moodTag.score
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_care),
                    modifier = Modifier.size(64.dp),
                    contentDescription = "Add mood tag"
                )
            }
        }
        Text(
            text = "#mood",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}