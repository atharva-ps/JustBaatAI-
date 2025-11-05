package com.example.justbaatai.catpreviousyear

import android.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.justbaatai.databinding.ItemExamCategoryBinding

class ExamCategoryAdapter(
    private val categories: List<ExamCategory>,
    private val onCategoryClick: (ExamCategory) -> Unit
) : RecyclerView.Adapter<ExamCategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0

    inner class CategoryViewHolder(private val binding: ItemExamCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: ExamCategory, position: Int) {
            binding.tvCategoryName.text = category.name
            binding.tvExamCount.text = "${category.examCount} Exams"

            // Set selected state colors
            if (position == selectedPosition) {
                binding.cardCategory.setCardBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.holo_blue_dark)
                )
            } else {
                binding.cardCategory.setCardBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.darker_gray)
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
        val binding = ItemExamCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], position)
    }

    override fun getItemCount() = categories.size
}