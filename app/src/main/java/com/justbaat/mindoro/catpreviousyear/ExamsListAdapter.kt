package com.justbaat.mindoro.catpreviousyear

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.justbaat.mindoro.databinding.ItemExamBinding

class ExamsListAdapter(
    private val exams: List<Exam>,
    private val onExamClick: (Exam) -> Unit
) : RecyclerView.Adapter<ExamsListAdapter.ExamViewHolder>() {

    inner class ExamViewHolder(private val binding: ItemExamBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(exam: Exam) {
            binding.tvExamName.text = exam.name
            binding.tvPapersCount.text = "${exam.papersCount} Previous Year Papers"
            binding.ivExamIcon.setImageResource(exam.iconRes)

            binding.root.setOnClickListener {
                onExamClick(exam)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        val binding = ItemExamBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ExamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        holder.bind(exams[position])
    }

    override fun getItemCount() = exams.size
}