package com.svbackend.natai.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.ui.NTextField
import com.svbackend.natai.android.ui.NTextarea
import com.svbackend.natai.android.ui.NataiTheme
import com.svbackend.natai.android.viewmodel.NoteViewModel
import kotlinx.coroutines.launch

class NewNoteActivity : ScopedActivity() {

    private val viewModel by viewModels<NoteViewModel>()

    private var title = ""
    private var content = ""

    private fun addNote(): () -> Unit {
        return {
            if (title.isEmpty() || content.isEmpty()) {
            }

            launch {
                viewModel.repository.insert(
                    Note(
                        title = title,
                        content = content,
                    )
                )
            }.invokeOnCompletion {
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
                            label = getString(R.string.noteTitle),
                            onChange = { title = it }
                        )
                        NTextarea(
                            label = getString(R.string.noteContent),
                            onChange = { content = it }
                        )
                        Button(onClick = addNote()) {
                            Text(getString(R.string.addNote))
                        }
                    }
                }
            }
        }
    }
}
