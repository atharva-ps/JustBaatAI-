package com.example.justbaatai.catexambooks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.justbaatai.databinding.ItemBookCategoryBinding

class BookCategoryAdapter(
    private val categories: List<BookCategory>,
    private val onCategoryClick: (BookCategory) -> Unit
) : RecyclerView.Adapter<BookCategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0

    inner class CategoryViewHolder(private val binding: ItemBookCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: BookCategory, position: Int) {
            binding.tvCategoryName.text = category.categoryName
            binding.tvBooksCount.text = "${category.booksCount} Smartbooks"

            // Set selected state colors
            if (position == selectedPosition) {
                binding.cardCategory.setCardBackgroundColor(
                    ContextCompat.getColor(binding.root.context, android.R.color.holo_blue_dark)
                )
            } else {
                binding.cardCategory.setCardBackgroundColor(
                    ContextCompat.getColor(binding.root.context, android.R.color.darker_gray)
                )
            }

            binding.root.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onCategoryClick(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemBookCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], position)
    }

    override fun getItemCount() = categories.size
}
