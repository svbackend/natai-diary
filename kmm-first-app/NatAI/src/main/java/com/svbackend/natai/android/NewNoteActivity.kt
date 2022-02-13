package com.svbackend.natai.android

import android.os.Bundle
import androidx.activity.viewModels
import com.svbackend.natai.android.databinding.ActivityNewNoteBinding
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.viewmodel.NoteViewModel
import kotlinx.coroutines.launch

class NewNoteActivity : ScopedActivity() {

    private lateinit var binding: ActivityNewNoteBinding

    private val viewModel by viewModels<NoteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addNoteBtn.setOnClickListener {
            launch {
                viewModel.repository.insert(
                    Note(
                        title = binding.txtNoteTitle.text.toString(),
                        content = binding.txtNoteContent.text.toString(),
                    )
                )
            }.invokeOnCompletion {
                finish()
            }
        }
    }
}