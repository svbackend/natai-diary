package com.svbackend.natai.android.ui.component

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.svbackend.natai.android.R
import com.svbackend.natai.android.entity.TagEntityDto
import com.svbackend.natai.android.ui.NTextField

@Composable
fun TagsField(
    context: Context,
    value: TextFieldValue,
    tags: List<TagEntityDto>,
    onAddTag: (TagEntityDto) -> Unit,
    onDeleteTag: (TagEntityDto) -> Unit,
    onValueChange: (TextFieldValue) -> Unit,
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    val tagsScrollState = rememberLazyListState()

    fun addTag(tag: String) {
        val newTag = sanitizeTag(tag)

        if (newTag.isNotEmpty() && newTag.length > 1) {
            onAddTag(TagEntityDto.create(newTag))
        }
    }

    val onChange = { v: TextFieldValue ->
        onValueChange(v)
        val newValue = v.text
        val lastChar = newValue.lastOrNull()
        if (sanitizeTag(newValue).isNotEmpty() && (lastChar == ',' || lastChar == ' ')) {
            addTag(newValue)
        }
    }

    CustomTagsBadges(
        tags = tags,
        onDeleteTag = onDeleteTag,
        preserveSpace = false,
    )

    if (isDialogOpen) {
        AlertDialog(
            onDismissRequest = { isDialogOpen = false },
            confirmButton = {
                Button(onClick = {
                    addTag(value.text)
                    isDialogOpen = false
                }) {
                    Text(stringResource(R.string.done))
                }
            },
            text = {
                Column {
                    NTextField(
                        value = value,
                        label = stringResource(R.string.noteTags),
                        onChange = onChange,
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    addTag(value.text)
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = "Add tag"
                                )
                            }
                        }
                    )

                    CustomTagsBadges(
                        tags = tags,
                        onDeleteTag = onDeleteTag,
                        preserveSpace = true,
                    )

                }
            },
            title = {
                Text("Add tag")
            }
        )
    }


    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            IconButton(
                onClick = {

                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Face,
                    contentDescription = "Add mood tag"
                )
            }
            Text(
                text = "#mood",
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Column(verticalArrangement = Arrangement.Center) {
            IconButton(
                onClick = {

                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Face,
                    contentDescription = "Add sleep tag"
                )
            }
            Text(
                text = "#sleep",
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Column(verticalArrangement = Arrangement.Center) {
            IconButton(
                onClick = {

                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Face,
                    contentDescription = "Add sport tag"
                )
            }
            Text(
                text = "#sport",
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Column(verticalArrangement = Arrangement.Center) {
            IconButton(
                onClick = {
                    isDialogOpen = true
                    onValueChange(TextFieldValue(""))
                },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_new_label),
                    contentDescription = "Add custom tag"
                )
            }
            Text(
                text = "Add Tag",
                style = MaterialTheme.typography.bodySmall,
            )
        }

    }
}

@Composable
fun CustomTagsBadges(
    tags: List<TagEntityDto>,
    onDeleteTag: (TagEntityDto) -> Unit,
    preserveSpace: Boolean = false,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            state = rememberLazyListState(),
        ) {
            if (preserveSpace && tags.isEmpty()) {
                // Workaround to fix bug when adding first tag it is not showing up in dialog box
                item {
                    TagBadge(
                        modifier = Modifier.alpha(0f),
                        tag = TagEntityDto("No Tags"),
                        onDelete = {}
                    )
                }
            }

            tags.map { tag ->
                if (!tag.isSpecial) {
                    item {
                        TagBadge(
                            tag = tag,
                            onDelete = {
                                onDeleteTag(tag)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagBadge(
    modifier: Modifier = Modifier,
    tag: TagEntityDto,
    onDelete: () -> Unit,
) {
    InputChip(
        modifier = modifier.padding(end = 4.dp),
        onClick = {},
        label = { Text(text = tag.name) },
        trailingIcon = {
            IconButton(
                modifier = Modifier.size(14.dp),
                onClick = onDelete,
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = "Delete tag"
                )
            }
        }
    )
}

fun sanitizeTag(tag: String): String {
    return TagEntityDto.cleanTag(tag)
}