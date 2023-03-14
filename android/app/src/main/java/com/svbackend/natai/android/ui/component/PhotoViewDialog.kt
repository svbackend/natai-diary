package com.svbackend.natai.android.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.svbackend.natai.android.R
import com.svbackend.natai.android.entity.ExistingLocalAttachmentDto

// Zoomable image, you can swipe left/right to change current attachment
@Composable
fun PhotoViewDialog(
    attachment: ExistingLocalAttachmentDto,
    onClose: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit
) {
    BackHandler(onBack = onClose)

    val currentPhoto = attachment.uri
    var scale by remember { mutableStateOf(1f) }
    var offsetState by remember { mutableStateOf(Offset.Zero) }
    val scaleState = rememberTransformableState { zoomChange, panChange, _ ->
        // Don't allow zooming out past original size
        val newScale = scale * zoomChange
        if (newScale in 1f..4f) {
            scale = newScale
        }

        if (newScale < 1.1f) {
            offsetState = Offset.Zero
        }

        if (newScale > 1.1f) {
            val newOffset = offsetState + panChange
            // Don't allow panning past the edges
            offsetState = newOffset
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .zIndex(10f)
            .background(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
    ) {
        Image(
            painter = rememberAsyncImagePainter(currentPhoto),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            if (scale > 1.1f) {
                                scale = 1f
                                offsetState = Offset.Zero
                            } else {
                                scale = 2f
                            }
                        },
                        onTap = {
                            if (it.x < size.width / 2) {
                                onPrev()
                            } else {
                                onNext()
                            }
                        }
                    )
                }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetState.x,
                    translationY = offsetState.y
                )
                .transformable(scaleState, true),
            contentScale = ContentScale.Fit
        )
    }

    ImageButtons(onNext, onPrev)
}

@Composable
fun ImageButtons(onNext: () -> Unit, onPrev: () -> Unit) {
    val colors = IconButtonDefaults.iconButtonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    )
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .fillMaxSize()
            .zIndex(20f)
    ) {
        IconButton(onClick = onPrev, colors = colors) {
            Icon(Icons.Filled.ArrowBack, "Prev")
        }

        IconButton(onClick = onNext, colors = colors) {
            Icon(Icons.Filled.ArrowForward, "Next")
        }
    }
}