package com.svbackend.natai.android.ui.component

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.svbackend.natai.android.R
import com.svbackend.natai.android.entity.ExistingAttachmentDto
import com.svbackend.natai.android.entity.Tag
import com.svbackend.natai.android.entity.TagEntityDto
import com.svbackend.natai.android.ui.NTextField
import com.svbackend.natai.android.viewmodel.AddFileViewModel
import kotlin.math.roundToInt

@Composable
fun TagsAndFilesRow(
    tagsSuggestions: List<String>,
    value: TextFieldValue,
    tags: List<TagEntityDto>,
    onAddTag: (TagEntityDto) -> Unit,
    onDeleteTag: (TagEntityDto) -> Unit,
    onValueChange: (TextFieldValue) -> Unit,
    addFileVm: AddFileViewModel,
    existingAttachments: List<ExistingAttachmentDto> = emptyList(),
    onDeleteExistingAttachment: (ExistingAttachmentDto) -> Unit = {},
) {
    var isAddCustomTagDialogOpen by remember { mutableStateOf(false) }

    var isSpecialTagDialogOpen by remember { mutableStateOf(false) }
    var selectedSpecialTag by remember { mutableStateOf<String?>(null) }
    var selectedScore by remember { mutableStateOf<Int?>(null) }

    var isTagScoreDialogOpen by remember { mutableStateOf(false) }
    var selectedTag by remember { mutableStateOf<TagEntityDto?>(null) }
    var selectedTagScore by remember { mutableStateOf(10) }

    val addedFiles by addFileVm.addedFiles
    val addedFilesSize = addFileVm.addedFiles.value.count() + existingAttachments.count()

    val pickFilesLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { imageUris ->
        addFileVm.onAdd(imageUris)
    }

    val onLaunchFilePicker = {
        pickFilesLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    val openTagScoreDialog = fun(tag: TagEntityDto) {
        selectedTag = tag
        isTagScoreDialogOpen = true
        selectedTagScore = tag.score ?: 10
    }

    val openSpecialTagDialog = fun(tag: String) {
        selectedSpecialTag = tag
        selectedScore = tags.find { it.tag == tag }?.score
        isSpecialTagDialogOpen = true
    }

    val moodTag = tags.find { it.tag == "mood" }
    val weatherTag = tags.find { it.tag == "weather" }

    val suggestions = tagsSuggestions
        .filter { suggestion ->
            !Tag.isSpecial(suggestion)
                    && tags.any { it.tag == suggestion }.not()
                    && (value.text.isEmpty() || suggestion.lowercase()
                .startsWith(value.text.lowercase()))
        }
        .take(5)

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
        openTagScoreDialog = openTagScoreDialog,
    )

    if (isTagScoreDialogOpen && selectedTag != null) {
        AlertDialog(
            onDismissRequest = { isTagScoreDialogOpen = false },
            confirmButton = {
                Button(onClick = {
                    onAddTag(selectedTag!!.copy(score = selectedTagScore))
                    isTagScoreDialogOpen = false
                }) {
                    Text(stringResource(R.string.done))
                }
            },
            text = {
                Slider(
                    value = selectedTagScore.div(10f),
                    onValueChange = { selectedTagScore = it.times(10f).roundToInt() },
                    steps = 9
                )
            },
            title = {
                Text("#${selectedTag!!.tag} ($selectedTagScore)")
            }
        )
    }

    if (isSpecialTagDialogOpen && selectedSpecialTag != null) {
        SpecialTagDialog(
            tag = selectedSpecialTag!!,
            score = selectedScore,
            onSelect = { score: Int ->
                onAddTag(
                    TagEntityDto(
                        tag = selectedSpecialTag!!,
                        score = score
                    )
                )
            },
            onConfirm = {
                isSpecialTagDialogOpen = false
            },
        )
    }

    if (isAddCustomTagDialogOpen) {
        AlertDialog(
            onDismissRequest = { isAddCustomTagDialogOpen = false },
            confirmButton = {
                Button(onClick = {
                    addTag(value.text)
                    isAddCustomTagDialogOpen = false
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
                        openTagScoreDialog = { tag ->
                            isTagScoreDialogOpen = true
                            selectedTag = tag
                        }
                    )

                }
            },
            title = {
                Text("Add tag")
            }
        )
    }

    if (addFileVm.isAddFileDialogOpen.value) {
        AddFileDialog(
            onLaunchFilePicker = {
                onLaunchFilePicker()
            },
            selectedFiles = addedFiles,
            onClose = {
                addFileVm.onClose()
            },
            onDelete = {
                addFileVm.onDelete(it)
            },
            existingAttachments = existingAttachments,
            onDeleteExistingAttachment = onDeleteExistingAttachment,
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row {
            MoodTagButton(
                moodTag = moodTag,
                onClick = {
                    openSpecialTagDialog("mood")
                },
                modifier = Modifier.padding(end = 8.dp),
            )

            WeatherTagButton(
                weatherTag = weatherTag,
                onClick = {
                    openSpecialTagDialog("weather")
                }
            )
        }

        Row(horizontalArrangement = Arrangement.End) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AddFileButton(addedFilesSize = addedFilesSize) {
                    if (addedFilesSize == 0) {
                        onLaunchFilePicker()
                    } else {
                        addFileVm.onOpen()
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AddCustomTagButton {
                    isAddCustomTagDialogOpen = true
                    onValueChange(TextFieldValue(""))
                }
            }
        }
    }
}

val BTN_NEGATIVE_OFFSET = (-12).dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFileButton(addedFilesSize: Int, onClick: () -> Unit) {
    val alpha = if (addedFilesSize > 0) {
        1f
    } else {
        0f
    }
    IconButton(
        modifier = Modifier.size(64.dp),
        onClick = onClick,
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            BadgedBox(
                badge = {
                    Badge(modifier = Modifier.alpha(alpha)) {
                        Text(addedFilesSize.toString())
                    }
                },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.cloud_arrow_up),
                    contentDescription = "Add file"
                )
            }
        }
    }

    Text(
        modifier = Modifier.offset(y = BTN_NEGATIVE_OFFSET),
        text = "Add File",
        style = MaterialTheme.typography.bodySmall,
    )
}

@Composable
fun AddCustomTagButton(onClick: () -> Unit) {
    IconButton(
        modifier = Modifier.size(64.dp),
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_new_label),
            contentDescription = "Add custom tag"
        )
    }
    Text(
        modifier = Modifier.offset(y = BTN_NEGATIVE_OFFSET),
        text = "Add Tag",
        style = MaterialTheme.typography.bodySmall,
    )
}

@Composable
fun SpecialTagDialog(tag: String, score: Int?, onSelect: (Int) -> Unit, onConfirm: () -> Unit) {
    val onChange = { value: Int ->
        onSelect(value)
        onConfirm()
    }

    AlertDialog(
        onDismissRequest = { onConfirm() },
        confirmButton = {
            Button(onClick = {
                onConfirm()
            }) {
                Text(stringResource(R.string.done))
            }
        },
        text = {
            when (tag) {
                "mood" -> {
                    MoodTagDialog(score, onChange)
                }
                "weather" -> {
                    WeatherTagDialog(score, onChange)
                }
            }
        },
        title = {
            Text("#$tag")
        }
    )
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
                    selected = true,
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
    openTagScoreDialog: (TagEntityDto) -> Unit,
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
                        onDelete = {},
                        onClick = {},
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
                            },
                            onClick = {
                                openTagScoreDialog(tag)
                            },
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
    val scrollState = rememberScrollState()
    val regularTags = tags.filter { !it.isSpecial }
    Row(modifier = Modifier.horizontalScroll(scrollState)) {
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
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    InputChip(
        selected = true,
        modifier = modifier.padding(end = 4.dp),
        onClick = onClick,
        label = { Text(text = tag.tag) },
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
    when (tag.tag) {
        "mood" -> {
            SpecialTagPreviewBadge(
                modifier = modifier,
                tag = tag,
            )
        }
        "weather" -> {
            SpecialTagPreviewBadge(
                modifier = modifier,
                tag = tag,
            )
        }
        else -> InputChip(
            selected = true,
            modifier = modifier.padding(end = 4.dp),
            onClick = {},
            label = {
                Text(
                    text = "#${tag.tag}",
                    modifier = Modifier.widthIn(min = 32.dp),
                    maxLines = 1
                )
            },
        )
    }
}

@Composable
fun SpecialTagPreviewBadge(
    modifier: Modifier = Modifier,
    tag: TagEntityDto,
) {
    SpecialTagIcon(modifier = modifier.size(64.dp), tag = tag.tag, score = tag.score)
}

fun sanitizeTag(tag: String): String {
    return TagEntityDto.cleanTag(tag)
}

@Composable
fun SpecialTagIcon(modifier: Modifier, tag: String, score: Int?) {
    when (tag) {
        "mood" -> MoodTagIcon(modifier, score)
        "weather" -> WeatherTagIcon(modifier, score)
    }
}