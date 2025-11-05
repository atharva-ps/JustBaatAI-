package com.example.justbaatai.catcurrentaffairs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.justbaatai.R
import com.example.justbaatai.databinding.ItemCourseBinding
import com.google.android.material.chip.Chip

class CoursesAdapter(
    private val onCourseClick: (Course) -> Unit
) : ListAdapter<Course, CoursesAdapter.CourseViewHolder>(CourseDiffCallback()) {

    inner class CourseViewHolder(private val binding: ItemCourseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var currentFeatures: List<String> = emptyList()

        fun bind(course: Course) {
            binding.tvCourseTitle.text = course.title

            // Load course image
            binding.ivCourseImage.load(course.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_exam_placeholder)
                error(R.drawable.ic_exam_placeholder)
            }

            // Only update chips if features have changed
            if (currentFeatures != course.features) {
                currentFeatures = course.features
                updateChips(course.features)
            }

            binding.root.setOnClickListener {
                onCourseClick(course)
            }
        }

        private fun updateChips(features: List<String>) {
            binding.chipGroupFeatures.removeAllViews()
            features.forEach { feature ->
                val chip = Chip(binding.root.context).apply {
                    text = feature
                    textSize = 10f
                    setTextColor(ContextCompat.getColor(context, android.R.color.white))
                    chipBackgroundColor = ContextCompat.getColorStateList(
                        context,
                        if (feature.contains("LIVE", ignoreCase = true))
                            android.R.color.holo_red_dark
                        else android.R.color.holo_orange_dark
                    )
                    isClickable = false
                }
                binding.chipGroupFeatures.addView(chip)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CourseDiffCallback : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.title == newItem.title // Use a unique ID if available
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }
    }
}