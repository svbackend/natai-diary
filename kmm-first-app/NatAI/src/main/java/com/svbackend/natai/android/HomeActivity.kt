package com.svbackend.natai.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.svbackend.natai.android.databinding.ActivityHomeBinding
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.viewmodel.NoteViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeActivity : ScopedActivity() {
    private lateinit var binding: ActivityHomeBinding

//    private lateinit var appBarConfiguration: AppBarConfiguration
//    private lateinit var navController: NavController

    private val viewModel by viewModels<NoteViewModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

//        navController = findNavController(R.id.nav_host_fragment_content_home)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.addNoteBtn.setOnClickListener {
            val intent = Intent(this, NewNoteActivity::class.java)
            startActivity(intent)
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
        }

        loadNotes()
    }

    private fun loadNotes() = launch {
        val onClick = OnClickListener<Note> {
            val intent = Intent(this@HomeActivity, NewNoteActivity::class.java)
            startActivity(intent)
        }

        viewModel.notes.collect { notes ->
            viewManager = GridLayoutManager(application, 1)
            viewAdapter = NoteAdapter(notes, onClick)

            println("=============COLLECT CALLED FROM FRAGMENT=============")

            recyclerView = (findViewById<RecyclerView>(R.id.NotesRecyclerView)).apply {
                    // use this setting to improve performance if you know that changes
                    // in content do not change the layout size of the RecyclerView
                    setHasFixedSize(true)

                    // use a linear layout manager
                    layoutManager = viewManager

                    // specify an viewAdapter (see also next example)
                    adapter = viewAdapter
                }

            return@collect
        }
    }
}