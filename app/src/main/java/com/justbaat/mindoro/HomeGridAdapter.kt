package com.justbaat.mindoro.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.justbaat.mindoro.HomeGridItem
import com.justbaat.mindoro.databinding.GridItemHomeBinding

class HomeGridAdapter(private val onItemClicked: (HomeGridItem) -> Unit) :
    ListAdapter<HomeGridItem, HomeGridAdapter.ViewHolder>(HomeGridDiffCallback()) {

    inner class ViewHolder(private val binding: GridItemHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeGridItem) {
            binding.itemTitle.text = item.title
            binding.itemSubtitle.text = item.subtitle
            binding.itemIcon.setImageResource(item.iconResId)
            val color = ContextCompat.getColor(itemView.context, item.backgroundColorResId)
            binding.cardView.setCardBackgroundColor(color)

            // Show/hide badge
            binding.itemNewTag.isVisible = item.tag != null
            binding.itemNewTag.text = item.tag

            itemView.setOnClickListener { onItemClicked(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GridItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class HomeGridDiffCallback : DiffUtil.ItemCallback<HomeGridItem>() {
    override fun areItemsTheSame(oldItem: HomeGridItem, newItem: HomeGridItem): Boolean {
        return oldItem.actionId == newItem.actionId
    }

    override fun areContentsTheSame(oldItem: HomeGridItem, newItem: HomeGridItem): Boolean {
        return oldItem == newItem
    }
}