package com.example.justbaatai

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.justbaatai.databinding.ItemLanguageBinding

data class Language(val name: String, val code: String)

class LanguageAdapter : ListAdapter<Language, LanguageAdapter.ViewHolder>(LanguageDiffCallback()) {

    var selectedPosition = 0

    inner class ViewHolder(val binding: ItemLanguageBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    // Update selection
                    notifyItemChanged(selectedPosition) // Uncheck old
                    selectedPosition = adapterPosition
                    notifyItemChanged(selectedPosition) // Check new
                }
            }
        }

        fun bind(language: Language) {
            binding.languageName.text = language.name
            binding.languageRadioButton.isChecked = (selectedPosition == adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLanguageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Use getItem() to get the current language
        holder.bind(getItem(position))
    }

    fun getSelectedLanguage(): Language? {
        return if (currentList.isNotEmpty()) {
            getItem(selectedPosition)
        } else {
            null
        }
    }
}

class LanguageDiffCallback : DiffUtil.ItemCallback<Language>() {
    override fun areItemsTheSame(oldItem: Language, newItem: Language): Boolean {
        // In a real app, you'd compare unique IDs, e.g., oldItem.id == newItem.id
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: Language, newItem: Language): Boolean {
        // Check if all the content is the same
        return oldItem == newItem
    }
}