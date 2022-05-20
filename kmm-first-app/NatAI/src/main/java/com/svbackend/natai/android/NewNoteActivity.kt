package com.svbackend.natai.android

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.ui.NProgressBtn
import com.svbackend.natai.android.ui.NTextField
import com.svbackend.natai.android.ui.NTextarea
import com.svbackend.natai.android.ui.NataiTheme
import com.svbackend.natai.android.viewmodel.NewNoteViewModel
import kotlinx.coroutines.launch

class NewNoteActivity : ScopedActivity() {

    private val viewModel by viewModels<NewNoteViewModel>()

    private fun addNote(): () -> Unit {
        if (viewModel.title.value.text.isEmpty() || viewModel.content.value.text.isEmpty()) {
            return {
                Toast
                    .makeText(this, "Title and content are required", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return {
            viewModel.isLoading.value = true
            launch {
                viewModel.repository.insert(
                    Note(
                        title = viewModel.title.value.text,
                        content = viewModel.content.value.text,
                    )
                )
            }.invokeOnCompletion {
                viewModel.isLoading.value = false
                finish()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NataiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = getString(R.string.newNote),
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                        )
                        NTextField(
                            value = viewModel.title.value,
                            label = getString(R.string.noteTitle),
                            onChange = { viewModel.title.value = it }
                        )
                        NTextarea(
                            value = viewModel.content.value,
                            label = getString(R.string.noteContent),
                            onChange = { viewModel.content.value = it }
                        )
                        if (viewModel.isLoading.value) {
                            Button(onClick = {}) {
                                NProgressBtn()
                                Text(
                                    text = getString(R.string.saving),
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        } else {
                            Button(onClick = addNote()) {
                                Text(text = getString(R.string.addNote))
                            }
                        }

                    }

                }
            }
        }
    }
}
