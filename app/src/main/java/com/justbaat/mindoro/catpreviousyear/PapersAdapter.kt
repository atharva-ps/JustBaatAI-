package com.justbaat.mindoro.catpreviousyear

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.justbaat.mindoro.R
import com.justbaat.mindoro.databinding.ItemPaperBinding
import com.justbaat.mindoro.databinding.ItemYearHeaderBinding

class PapersAdapter(
    private val yearGroups: MutableList<YearGroup>,
    private val onDownloadClick: (Paper) -> Unit,
    private val onUnlockClick: (Paper) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_YEAR_HEADER = 0
        private const val TYPE_PAPER = 1
    }

    private data class ListItem(
        val type: Int,
        val yearGroup: YearGroup? = null,
        val paper: Paper? = null,
        val yearPosition: Int = -1
    )

    private var items = mutableListOf<ListItem>()

    init {
        updateItems()
    }

    private fun updateItems() {
        items.clear()
        yearGroups.forEachIndexed { index, yearGroup ->
            // Add year header
            items.add(ListItem(TYPE_YEAR_HEADER, yearGroup = yearGroup, yearPosition = index))

            // Add papers if expanded
            if (yearGroup.isExpanded) {
                yearGroup.papers.forEach { paper ->
                    items.add(ListItem(TYPE_PAPER, paper = paper))
                }
            }
        }
    }

    inner class YearHeaderViewHolder(private val binding: ItemYearHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(yearGroup: YearGroup, position: Int) {
            binding.tvYear.text = yearGroup.year

            // Set expand/collapse icon
            binding.ivExpandCollapse.setImageResource(
                if (yearGroup.isExpanded) R.drawable.ic_expand_less
                else R.drawable.ic_expand_more
            )

            binding.yearHeaderLayout.setOnClickListener {
                yearGroup.isExpanded = !yearGroup.isExpanded
                updateItems()
                notifyDataSetChanged()
            }
        }
    }

    inner class PaperViewHolder(private val binding: ItemPaperBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(paper: Paper) {
            binding.tvPaperTitle.text = paper.title
            binding.tvPaperDetails.text =
                "${paper.questionsCount} Qs • ${paper.durationMinutes} mins • ${paper.totalMarks} Marks"
            binding.tvLanguages.text = paper.languages.joinToString(", ")

            binding.btnDownloadPdf.setOnClickListener {
                onDownloadClick(paper)
            }

            binding.btnUnlockTest.setOnClickListener {
                onUnlockClick(paper)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_YEAR_HEADER -> {
                val binding = ItemYearHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                YearHeaderViewHolder(binding)
            }
            else -> {
                val binding = ItemPaperBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                PaperViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is YearHeaderViewHolder -> {
                items[position].yearGroup?.let {
                    holder.bind(it, items[position].yearPosition)
                }
            }
            is PaperViewHolder -> {
                items[position].paper?.let { holder.bind(it) }
            }
        }
    }

    override fun getItemCount() = items.size
}
