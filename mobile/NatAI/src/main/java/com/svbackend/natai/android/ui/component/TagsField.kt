package com.svbackend.natai.android.ui.component

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.svbackend.natai.android.R
import com.svbackend.natai.android.entity.Tag
import com.svbackend.natai.android.entity.TagEntityDto
import com.svbackend.natai.android.ui.NTextField
import com.svbackend.natai.android.utils.gradientBackground
import kotlin.math.roundToInt

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
    var isAddCustomTagDialogOpen by remember { mutableStateOf(false) }

    var isSpecialTagDialogOpen by remember { mutableStateOf(false) }
    var selectedSpecialTag by remember { mutableStateOf<String?>(null) }
    var selectedScore by remember { mutableStateOf<Int?>(null) }

    var isTagScoreDialogOpen by remember { mutableStateOf(false) }
    var selectedTag by remember { mutableStateOf<TagEntityDto?>(null) }
    var selectedTagScore by remember { mutableStateOf(10) }

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

    val suggestions = tagsSuggestions
        .take(5)
        .filter { suggestion ->
            !Tag.isSpecial(suggestion)
                    && tags.any { it.tag == suggestion }.not()
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


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            IconButton(
                onClick = {
                    openSpecialTagDialog("mood")
                },
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
        Column(verticalArrangement = Arrangement.Center) {
            IconButton(
                onClick = {
                    isAddCustomTagDialogOpen = true
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
            }
        },
        title = {
            Text("#$tag")
        }
    )
}

@Composable
fun MoodTagDialog(selectedScore: Int?, onChange: (Int) -> Unit) {

    @Composable
    fun MoodTagEl(m: Modifier, score: Int) {
        MoodTagIcon(
            modifier = m
                .clickable { onChange(score) }, score = score, isSelected = (selectedScore == score)
        )
    }

    Column {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            MoodTagEl(Modifier.weight(1f), 10)
            MoodTagEl(Modifier.weight(1f), 9)
            MoodTagEl(Modifier.weight(1f), 8)
        }

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            MoodTagEl(Modifier.weight(1f), 7)
            MoodTagEl(Modifier.weight(1f), 6)
            MoodTagEl(Modifier.weight(1f), 5)
        }

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            MoodTagEl(Modifier.weight(1f), 4)
            MoodTagEl(Modifier.weight(1f), 3)
            MoodTagEl(Modifier.weight(1f), 2)
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
            MoodTagPreviewBadge(
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
fun MoodTagPreviewBadge(
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
    }
}

@Composable
fun MoodTagIcon(modifier: Modifier, score: Int?, isSelected: Boolean = false) {
    val iconsMap = remember {
        mapOf(
            10 to R.drawable.ic__10,
            9 to R.drawable.ic__9,
            8 to R.drawable.ic__8,
            7 to R.drawable.ic__7,
            6 to R.drawable.ic__6,
            5 to R.drawable.ic__5,
            4 to R.drawable.ic__4,
            3 to R.drawable.ic__3,
            2 to R.drawable.ic__2,
            1 to R.drawable.ic__2,
            0 to R.drawable.ic__2,
        )
    }

    val id = iconsMap[score] ?: R.drawable.ic_care


    val m = if (isSelected) {
        modifier
            .clip(CircleShape)
            .gradientBackground(
                listOf(
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.colorScheme.secondary,
                ), angle = 45f
            )
    } else {
        modifier
    }

    Image(
        painter = painterResource(id = id),
        modifier = m,
        contentDescription = null,
    )
}