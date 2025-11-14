package com.justbaat.mindoro.catfreequizzes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.justbaat.mindoro.databinding.ItemLiveTestBinding

class LiveTestsAdapter(
    private var tests: List<LiveTest>,
    private val onTestClick: (LiveTest) -> Unit
) : RecyclerView.Adapter<LiveTestsAdapter.LiveTestViewHolder>() {

    inner class LiveTestViewHolder(private val binding: ItemLiveTestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(test: LiveTest) {
            binding.tvFreeBadge.visibility = if (test.isFree) View.VISIBLE else View.GONE
            binding.tvLiveTestBadge.visibility = if (test.isLiveTest) View.VISIBLE else View.GONE

            binding.tvTags.text = test.tags.joinToString(", ")
            binding.tvTestTitle.text = test.title
            binding.tvTestDetails.text = "${test.questionsCount} Qs. ${test.durationMinutes} mins. ${test.totalMarks} Marks"
            binding.tvLanguages.text = test.languages.joinToString(", ")
            binding.tvEndsIn.text = "Ends in ${test.endsInDays} day${if (test.endsInDays > 1) "s" else ""}"
            binding.tvAction.text = test.status

            binding.root.setOnClickListener {
                onTestClick(test)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveTestViewHolder {
        val binding = ItemLiveTestBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return LiveTestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LiveTestViewHolder, position: Int) {
        holder.bind(tests[position])
    }

    override fun getItemCount() = tests.size

    fun updateTests(newTests: List<LiveTest>) {
        tests = newTests
        notifyDataSetChanged()
    }
}
