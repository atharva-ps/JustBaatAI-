package com.justbaat.mindoro.catexambooks


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.justbaat.mindoro.R
import com.justbaat.mindoro.databinding.ItemBookBinding
import com.google.android.material.chip.Chip

class BooksAdapter(
    private val books: List<Book>,
    private val onBookClick: (Book) -> Unit
) : RecyclerView.Adapter<BooksAdapter.BookViewHolder>() {

    inner class BookViewHolder(private val binding: ItemBookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            binding.tvBookTitle.text = book.title
            binding.tvLanguage.text = book.language
            binding.tvRecommended.text = "Recommended for ${book.examType}"

            // Load book cover image with Coil
            binding.ivBookCover.load(book.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_exam_placeholder)
                error(R.drawable.ic_exam_placeholder)
            }

            // Add subject chips
            binding.chipGroupSubjects.removeAllViews()
            book.subjects.forEach { subject ->
                val chip = Chip(binding.root.context).apply {
                    text = subject
                    textSize = 11f
                    setTextColor(ContextCompat.getColor(context, android.R.color.white))
                    chipBackgroundColor = ContextCompat.getColorStateList(
                        context,
                        if (subject.contains("Theory", ignoreCase = true))
                            android.R.color.holo_orange_dark
                        else android.R.color.holo_red_dark
                    )
                    isClickable = false
                }
                binding.chipGroupSubjects.addView(chip)
            }

            binding.root.setOnClickListener {
                onBookClick(book)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount() = books.size
}
