package com.example.justbaatai.catfreetests


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.justbaatai.databinding.ItemMockTestBinding

class MockTestsAdapter(
    private val tests: List<MockTest>,
    private val onStartTest: (MockTest) -> Unit
) : RecyclerView.Adapter<MockTestsAdapter.TestViewHolder>() {

    inner class TestViewHolder(private val binding: ItemMockTestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(test: MockTest) {
            binding.tvTestTitle.text = test.title
            binding.tvTestDescription.text = test.description
            binding.tvQuestionsCount.text = "${test.questionsCount} Questions"
            binding.tvDuration.text = "${test.durationMinutes} mins"
            binding.tvDifficulty.text = test.difficulty

            // Set difficulty color
            val difficultyColor = when (test.difficulty) {
                "Easy" -> ContextCompat.getColor(binding.root.context, android.R.color.holo_green_dark)
                "Medium" -> ContextCompat.getColor(binding.root.context, android.R.color.holo_orange_dark)
                "Hard" -> ContextCompat.getColor(binding.root.context, android.R.color.holo_red_dark)
                else -> ContextCompat.getColor(binding.root.context, android.R.color.darker_gray)
            }
            binding.tvDifficulty.setTextColor(difficultyColor)

            binding.btnStartTest.setOnClickListener {
                onStartTest(test)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val binding = ItemMockTestBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.bind(tests[position])
    }

    override fun getItemCount() = tests.size
}
