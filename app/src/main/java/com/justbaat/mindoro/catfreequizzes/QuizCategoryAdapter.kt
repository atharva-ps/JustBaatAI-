package com.justbaat.mindoro.catfreequizzes



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.justbaat.mindoro.databinding.ItemQuizCategoryBinding

class QuizCategoryAdapter(
    private val categories: List<QuizCategory>,
    private val onCategoryClick: (QuizCategory) -> Unit
) : RecyclerView.Adapter<QuizCategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0

    inner class CategoryViewHolder(private val binding: ItemQuizCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: QuizCategory, position: Int) {
            binding.tvCategoryName.text = category.categoryName
            binding.tvTestsQuizzes.text = "${category.testsCount} Tests. ${category.quizzesCount} Quizzes"

            // Set selected state
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
        val binding = ItemQuizCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], position)
    }

    override fun getItemCount() = categories.size
}
