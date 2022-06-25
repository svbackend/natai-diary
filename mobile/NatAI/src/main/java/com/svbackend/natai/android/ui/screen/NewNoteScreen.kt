package com.svbackend.natai.android.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svbackend.natai.android.R
import com.svbackend.natai.android.ui.NProgressBtn
import com.svbackend.natai.android.ui.NTextField
import com.svbackend.natai.android.ui.NTextarea
import com.svbackend.natai.android.ui.component.TagsField
import com.svbackend.natai.android.utils.LocalDateTimeFormatter
import com.svbackend.natai.android.utils.throttleLatest
import com.svbackend.natai.android.viewmodel.NewNoteViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun NewNoteScreen(
    vm: NewNoteViewModel = viewModel(),
    onSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val tagsSuggestions by vm.tagsSuggestions.collectAsState(initial = emptyList())

    val onTitleChange: (String) -> Unit = throttleLatest(
        intervalMs = 350L,
        coroutineScope = scope,
        destinationFunction = vm::saveTitle
    )

    val onContentChange: (String) -> Unit = throttleLatest(
        intervalMs = 350L,
        coroutineScope = scope,
        destinationFunction = vm::saveContent
    )

    val actualDate = vm.actualDate.value
    val onDateChange = { date: LocalDate ->
        vm.actualDateChanged(date)
    }

    fun addNote(): () -> Unit {
        if (vm.title.value.text.isEmpty() || vm.content.value.text.isEmpty()) {
            return {
                Toast
                    .makeText(context, "Title and content are required", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return {
            scope.launch {
                vm.addNote()
                onSuccess()
            }
        }

    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = stringResource(R.string.newNote),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        onDateChange(actualDate.minusDays(1))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "select previous date"
                    )
                }
                Text(
                    text = LocalDateTimeFormatter.fullDate.format(actualDate),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
                IconButton(
                    onClick = {
                        onDateChange(actualDate.plusDays(1))
                    },
                    enabled = actualDate.isBefore(LocalDate.now())
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = "select next date"
                    )
                }
            }

            //NDateField(context = context, value = actualDate, onChange = onDateChange)
            NTextField(
                value = vm.title.value,
                label = stringResource(R.string.noteTitle),
                onChange = {
                    vm.title.value = it
                    onTitleChange(it.text)
                }
            )
            NTextarea(
                value = vm.content.value,
                label = stringResource(R.string.noteContent),
                onChange = {
                    vm.content.value = it
                    onContentChange(it.text)
                }
            )

            val tags by vm.tags
            val tagsValue = vm.tagsFieldValue.value

            TagsField(
                context = context,
                tags = tags,
                value = tagsValue,
                onAddTag = {
                    vm.addTag(it)
                    vm.tagsFieldValue.value = TextFieldValue("")
                },
                onDeleteTag = {
                    vm.deleteTag(it)
                },
                onValueChange = { vm.tagsFieldValue.value = it },
                tagsSuggestions = tagsSuggestions,
            )

            if (vm.isLoading.value) {
                Button(onClick = {}, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    NProgressBtn()
                    Text(
                        text = stringResource(R.string.saving),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            } else {
                Button(onClick = addNote(), modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Icon(
                        Icons.Filled.Add,
                        stringResource(R.string.addNote)
                    )
                    Text(
                        text = stringResource(R.string.addNote),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

        }
    }
}