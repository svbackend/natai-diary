package com.svbackend.natai.android.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import com.google.android.material.chip.Chip
import com.svbackend.natai.android.R
import com.svbackend.natai.android.entity.TagDto
import com.svbackend.natai.android.ui.NTextField

@Composable
fun TagsField(
    tags: List<TagDto>,
    onAddTag: (TagDto) -> Unit,
    onDeleteTag: (TagDto) -> Unit,
) {
    var value by remember { mutableStateOf(TextFieldValue("")) }

    fun addTag(tag: String) {
        val newTag = sanitizeTag(tag)

        if (newTag.isNotEmpty() && newTag.length > 1) {
            onAddTag(TagDto.create(newTag))
            value = TextFieldValue("")
        }
    }

    val onChange = { v: TextFieldValue ->
        value = v
        val newValue = v.text
        val lastChar = newValue.last()
        if (sanitizeTag(newValue).isNotEmpty() && (lastChar == ',' || lastChar == ' ')) {
            addTag(newValue)
        }
    }

    // Show list of tags as badges with delete button
    val tagsList = tags.map { tag ->
        TagBadge(tag, onDelete = {  })
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
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = "Add tag"
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagBadge(
    tag: TagDto,
    onDelete: () -> Unit
) {
    // jetpack compose tag badge with delete button
    SuggestionChip(
        onClick = {},
        label = { Text(text = tag.name) },
    )
}

fun sanitizeTag(tag: String): String {
    return tag
        .trimStart('#', ' ')
        .trimEnd()
        .replace(",", "")
}