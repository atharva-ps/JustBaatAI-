package com.example.justbaatai.catcurrentaffairs


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.justbaatai.R
import com.example.justbaatai.databinding.ItemSupercoachingCategoryBinding

class SuperCoachingCategoriesAdapter(
    private val categories: List<SuperCoachingCategory>,
    private val onCategoryClick: (SuperCoachingCategory) -> Unit
) : RecyclerView.Adapter<SuperCoachingCategoriesAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(private val binding: ItemSupercoachingCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: SuperCoachingCategory) {
            binding.tvCategoryTitle.text = category.title

            // Set icon based on category type
            val iconRes = when (category.icon) {
                "govt_emblem" -> R.drawable.ic_gov_emblem
                "teaching" -> R.drawable.ic_gov_emblem
                "ssc_hammer" -> R.drawable.ic_ssc_hammer
                else -> R.drawable.ic_gov_emblem
            }
            binding.ivCategoryIcon.setImageResource(iconRes)

            binding.root.setOnClickListener {
                onCategoryClick(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemSupercoachingCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size
}
