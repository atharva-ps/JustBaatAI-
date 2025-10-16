package com.example.justbaatai

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.justbaatai.databinding.ItemTestBinding

class TestsAdapter : ListAdapter<TestItem, TestsAdapter.ViewHolder>(TestDiffCallback()) {

    inner class ViewHolder(private val binding: ItemTestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(test: TestItem) {
            binding.testTitle.text = test.title
            binding.testQuestions.text = "${test.questionCount} Questions"
            binding.testDuration.text = "${test.durationMinutes} Mins"
            binding.startTestButton.setOnClickListener {
                Toast.makeText(itemView.context, "Starting ${test.title}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class TestDiffCallback : DiffUtil.ItemCallback<TestItem>() {
    override fun areItemsTheSame(oldItem: TestItem, newItem: TestItem): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: TestItem, newItem: TestItem): Boolean {
        return oldItem == newItem
    }
}