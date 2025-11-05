package com.example.justbaatai.catstudynotes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.justbaatai.databinding.ItemStudyNoteBinding

class StudyNotesAdapter : ListAdapter<StudyNote, StudyNotesAdapter.ViewHolder>(StudyNoteDiffCallback()) {

    inner class ViewHolder(private val binding: ItemStudyNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(note: StudyNote) {
            binding.noteTitle.text = note.title
            binding.noteDescription.text = note.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStudyNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class StudyNoteDiffCallback : DiffUtil.ItemCallback<StudyNote>() {
    override fun areItemsTheSame(oldItem: StudyNote, newItem: StudyNote): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StudyNote, newItem: StudyNote): Boolean {
        return oldItem == newItem
    }
}