package com.example.justbaatai

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.justbaatai.databinding.ItemNotificationBinding

class NotificationsAdapter :
    ListAdapter<String, NotificationsAdapter.ViewHolder>(NotificationDiffCallback()) {

    // The ViewHolder remains the same
    inner class ViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // ListAdapter gives us the current item with getItem()
        val notificationItem = getItem(position)
        holder.binding.notificationText.text = notificationItem
    }
}

// DiffUtil calculates the difference between two lists for you, which is very efficient.
class NotificationDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        // For a simple list of strings, the items themselves can be compared for identity.
        // For a list of objects, you would typically compare their unique IDs here.
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        // Since the item is just a string, if the items are the same, the contents are too.
        return oldItem == newItem
    }
}