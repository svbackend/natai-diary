package com.svbackend.natai.android

import android.os.Bundle
import androidx.activity.viewModels
import com.svbackend.natai.android.databinding.ActivityNoteDetailsBinding
import com.svbackend.natai.android.viewmodel.NoteViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NoteDetailsActivity : ScopedActivity() {
    private lateinit var binding: ActivityNoteDetailsBinding

//    private lateinit var appBarConfiguration: AppBarConfiguration
//    private lateinit var navController: NavController

    private val viewModel by viewModels<NoteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

//        navController = findNavController(R.id.nav_host_fragment_content_home)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.editNoteBtn.setOnClickListener {
//            val intent = Intent(this, NewNoteActivity::class.java)
//            startActivity(intent)
        }

        binding.noteTitle.setText("Note not selected")
        binding.noteContent.setText("Note not selected")

        val noteId = intent.getStringExtra(PARAM_NOTE_ID)
        if (noteId != null) {
            loadNote(noteId)
        }
    }

    private fun loadNote(noteId: String) = launch {
        val note = viewModel.getNote(noteId)

        binding.noteTitle.setText(note.title)
        binding.noteContent.setText(note.content)
    }
}