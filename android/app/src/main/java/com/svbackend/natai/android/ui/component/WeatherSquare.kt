package com.svbackend.natai.android.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.entity.TagEntityDto
import com.svbackend.natai.android.ui.NataiCustomColors

@Composable
fun WeatherSquare(
    day: String,
    tag: String,
    notes: List<LocalNote>,
    onClick: (String) -> Unit,
) {
    var tagDto: TagEntityDto? = null
    notes.forEach { localNote ->
        localNote.tags.forEach innerLoop@{ t ->
            if (t.tag == tag) {
                tagDto = t
                return@innerLoop
            }
        }

        if (tagDto != null) {
            return@forEach
        }
    }

    val colors = NataiCustomColors.get()
    Box(
        modifier = Modifier
            .clickable { onClick(day) }
            .size(36.dp)
            .padding(horizontal = 2.dp, vertical = 2.dp)
            //.border(color = colors.border, width = 1.dp, shape = RoundedCornerShape(4.dp))
            .clip(shape = RoundedCornerShape(4.dp))
            .background(colors.emptyContribution)
    ) {
        if (tagDto != null) {
            SpecialTagIcon(modifier = Modifier.size(36.dp), tag = tag, score = tagDto!!.score)
        }
    }
}
