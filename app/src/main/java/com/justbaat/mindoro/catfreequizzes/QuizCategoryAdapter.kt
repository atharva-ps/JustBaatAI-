package com.justbaat.mindoro.catfreequizzes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.justbaat.mindoro.R
import com.justbaat.mindoro.databinding.ItemQuizCategoryBinding

class QuizCategoryAdapter(
    private var categories: List<QuizCategory>,
    private val onCategoryClick: (QuizCategory) -> Unit
) : RecyclerView.Adapter<QuizCategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = -1 // No category selected initially

    inner class CategoryViewHolder(private val binding: ItemQuizCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: QuizCategory, position: Int) {
            binding.tvCategoryName.text = category.categoryName
            binding.tvTestsQuizzes.text = "${category.testsCount} Tests. ${category.quizzesCount} Quizzes"

            // Theme-aware selection
            if (position == selectedPosition) {
                binding.cardCategory.setCardBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.purple_500)
                )
                binding.tvCategoryName.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.white)
                )
                binding.tvTestsQuizzes.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.white)
                )
            } else {
                binding.cardCategory.setCardBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.edit_text_background)
                )
                binding.tvCategoryName.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.text_primary)
                )
                binding.tvTestsQuizzes.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.text_secondary)
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
        val binding = ItemQuizCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], position)
    }

    override fun getItemCount() = categories.size

    fun updateCategories(newCategories: List<QuizCategory>) {
        categories = newCategories
        selectedPosition = -1 // Reset selection when categories update
        notifyDataSetChanged()
    }
}