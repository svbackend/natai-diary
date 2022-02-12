package com.svbackend.natai.android

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.svbackend.natai.android.databinding.NotePreviewCardBinding
import com.svbackend.natai.android.entity.Note

class NoteAdapter(private val notes: List<Note>) : RecyclerView.Adapter<NoteAdapter.NoteCardViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
//    class NoteCardViewHolder(notePreviewCardBinding: NotePreviewCardBinding) : RecyclerView.ViewHolder(notePreviewCardBinding)

    class NoteCardViewHolder(private val itemBinding: NotePreviewCardBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(note: Note) {
            itemBinding.noteTitle.text = note.title
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NoteCardViewHolder {
        // create a new view
        val noteCardView = NotePreviewCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // set the view's size, margins, paddings and layout parameters

        return NoteCardViewHolder(noteCardView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: NoteCardViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val note = notes[position]
        holder.bind(note)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = notes.size
}