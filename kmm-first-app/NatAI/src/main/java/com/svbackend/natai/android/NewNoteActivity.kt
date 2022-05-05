package com.svbackend.natai.android

import android.os.Bundle
import android.text.Editable
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

        val prefs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)

        val storedAccessToken = prefs.getString("access_token", null) ?: ""

        binding = ActivityNewNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtNoteTitle.setText(storedAccessToken)

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