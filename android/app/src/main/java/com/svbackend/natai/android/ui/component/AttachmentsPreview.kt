package com.svbackend.natai.android.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.svbackend.natai.android.R
import com.svbackend.natai.android.entity.AttachmentEntityDto

@Composable
fun AttachmentsPreview(attachments: List<AttachmentEntityDto>) {
    if (attachments.isEmpty()) {
        return
    }

    val placeholder = painterResource(id = R.drawable.placeholder)
    val visibleAttachments = attachments.take(4)
    val numberOfHiddenAttachments = attachments.count() - visibleAttachments.count()
    val lastIdx = visibleAttachments.count() - 1

    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
    ) {
        visibleAttachments.forEachIndexed { idx, attachment ->
            val uri = attachment.previewUri ?: attachment.uri ?: placeholder
            val isLast = idx == lastIdx
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(end = 12.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = attachment.filename,
                        modifier = Modifier
                            .size(64.dp, 64.dp)
                            .clip(RoundedCornerShape(percent = 10))
                    )

                    if (numberOfHiddenAttachments > 0 && isLast) {
                        Text(
                            text = "+$numberOfHiddenAttachments",
                            modifier = Modifier
                                .clip(RoundedCornerShape(percent = 10))
                                .background(color = Color.Black.copy(alpha = 0.65f))
                                .padding(8.dp)
                        )
                    }
                }
            }
        }

        // not sure if it helps, but when scrolling fast some images are are jumping
        /*
        val transparentPlaceholders = 4 - visibleAttachments.count()
        repeat(transparentPlaceholders) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .alpha(0f)
                )
            }
        }
        */
    }
}