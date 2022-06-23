package com.svbackend.natai.android.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.svbackend.natai.android.R
import com.svbackend.natai.android.entity.TagEntityDto
import com.svbackend.natai.android.ui.NTextField

@Composable
fun TagsField(
    value: TextFieldValue,
    tags: List<TagEntityDto>,
    onAddTag: (TagEntityDto) -> Unit,
    onDeleteTag: (TagEntityDto) -> Unit,
    onValueChange: (TextFieldValue) -> Unit,
) {
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

    // Show list of tags as badges with delete button
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            state = rememberLazyListState()
        ) {
            tags.map { tag ->
                item {
                    TagBadge(tag, onDelete = {
                        onDeleteTag(tag)
                    })
                }
            }
        }
    }

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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagBadge(
    tag: TagEntityDto,
    onDelete: () -> Unit
) {
    InputChip(
        modifier = Modifier.padding(end = 4.dp),
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