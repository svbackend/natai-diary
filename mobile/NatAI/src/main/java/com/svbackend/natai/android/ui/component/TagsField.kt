package com.svbackend.natai.android.ui.component

import android.content.Context
import androidx.compose.foundation.layout.*
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
import com.svbackend.natai.android.R
import com.svbackend.natai.android.entity.Tag
import com.svbackend.natai.android.entity.TagEntityDto
import com.svbackend.natai.android.ui.NTextField

@Composable
fun TagsField(
    tagsSuggestions: List<String>,
    context: Context,
    value: TextFieldValue,
    tags: List<TagEntityDto>,
    onAddTag: (TagEntityDto) -> Unit,
    onDeleteTag: (TagEntityDto) -> Unit,
    onValueChange: (TextFieldValue) -> Unit,
) {
    var isDialogOpen by remember { mutableStateOf(false) }

    val suggestions = tagsSuggestions
        .take(5)
        .filter { suggestion ->
            !Tag.isSpecial(suggestion)
                    && tags.any { it.name == suggestion }.not()
                    && (value.text.isEmpty() || suggestion.startsWith(value.text))
        }

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
                    TagsSuggestions(
                        tagsSuggestions = suggestions,
                        onAddTag = { tag ->
                            addTag(tag)
                        })

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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsSuggestions(tagsSuggestions: List<String>, onAddTag: (String) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        state = rememberLazyListState(),
    ) {
        for (tag in tagsSuggestions) {
            item {
                InputChip(
                    modifier = Modifier.padding(end = 4.dp),
                    onClick = { onAddTag(tag) },
                    label = { Text(text = tag) },
                    trailingIcon = {
                        IconButton(
                            modifier = Modifier.size(14.dp),
                            onClick = { onAddTag(tag) },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = "add tag"
                            )
                        }
                    }
                )
            }
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

@Composable
fun AllTagsBadges(
    tags: List<TagEntityDto>,
) {
    val regularTags = tags.filter { !it.isSpecial }

    SpecialTagsRow(tags = tags)

    if (regularTags.isNotEmpty()) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            state = rememberLazyListState(),
        ) {
            regularTags.map { tag ->
                item {
                    TagPreviewBadge(
                        tag = tag,
                    )
                }

            }
        }
    }
}

@Composable
fun SpecialTagsRow(tags: List<TagEntityDto>) {
    val specialTags = tags.filter { it.isSpecial }
    Row {
        specialTags.map {
            TagPreviewBadge(
                tag = it,
            )
        }
    }
}

@Composable
fun RegularTagsRow(tags: List<TagEntityDto>) {
    val regularTags = tags.filter { !it.isSpecial }
    Row {
        regularTags.map {
            TagPreviewBadge(
                tag = it,
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagPreviewBadge(
    modifier: Modifier = Modifier,
    tag: TagEntityDto,
) {
    InputChip(
        modifier = modifier.padding(end = 4.dp),
        onClick = {},
        label = { Text(text = "#${tag.name}") },
    )
}

fun sanitizeTag(tag: String): String {
    return TagEntityDto.cleanTag(tag)
}