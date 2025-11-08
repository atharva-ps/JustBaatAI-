package com.example.justbaatai.catnotification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.justbaatai.R
import com.example.justbaatai.databinding.ItemNotificationBinding

class NotificationsAdapter(
    private val onItemClick: (Notification) -> Unit,
    private val onMarkRead: (String) -> Unit,
    private val onDelete: (String) -> Unit
) : ListAdapter<Notification, NotificationsAdapter.ViewHolder>(NotificationDiffCallback()) {

    inner class ViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: Notification) {
            binding.apply {
                // Title and Message
                notificationTitle.text = notification.title
                notificationMessage.text = notification.message
                notificationTime.text = getFormattedTime(notification.timestamp)

                // Set background color based on read status
                val backgroundColor = if (notification.isRead) {
                    ContextCompat.getColor(itemView.context, android.R.color.white)
                } else {
                    ContextCompat.getColor(itemView.context, R.color.notification_unread_bg)
                }
                notificationContainer.setBackgroundColor(backgroundColor)

                // Type icon and color - EXHAUSTIVE WHEN STATEMENT
                when (notification.type) {
                    NotificationType.TEST -> {
                        notificationTypeIcon.setImageResource(R.drawable.ic_test_badge)
                        notificationTypeIcon.setBackgroundColor(
                            ContextCompat.getColor(itemView.context, R.color.color_test_blue)
                        )
                    }
                    NotificationType.STUDY -> {
                        notificationTypeIcon.setImageResource(R.drawable.ic_study_badge)
                        notificationTypeIcon.setBackgroundColor(
                            ContextCompat.getColor(itemView.context, R.color.color_study_green)
                        )
                    }
                    NotificationType.COURSE -> {
                        notificationTypeIcon.setImageResource(R.drawable.ic_course_badge)
                        notificationTypeIcon.setBackgroundColor(
                            ContextCompat.getColor(itemView.context, R.color.color_course_purple)
                        )
                    }
                    NotificationType.ACHIEVEMENT -> {
                        notificationTypeIcon.setImageResource(R.drawable.ic_achievement_badge)
                        notificationTypeIcon.setBackgroundColor(
                            ContextCompat.getColor(itemView.context, R.color.color_achievement_gold)
                        )
                    }
                    NotificationType.REMINDER -> {
                        notificationTypeIcon.setImageResource(R.drawable.ic_reminder_badge)
                        notificationTypeIcon.setBackgroundColor(
                            ContextCompat.getColor(itemView.context, R.color.color_reminder_orange)
                        )
                    }
                    NotificationType.OFFER -> {
                        notificationTypeIcon.setImageResource(R.drawable.ic_offer_badge)
                        notificationTypeIcon.setBackgroundColor(
                            ContextCompat.getColor(itemView.context, R.color.color_offer_red)
                        )
                    }
                    NotificationType.GENERAL -> {
                        notificationTypeIcon.setImageResource(R.drawable.ic_general_badge)
                        notificationTypeIcon.setBackgroundColor(
                            ContextCompat.getColor(itemView.context, R.color.color_general_gray)
                        )
                    }
                }

                // Unread indicator
                unreadIndicator.isVisible = !notification.isRead

                // Click handlers
                root.setOnClickListener {
                    onMarkRead(notification.id)
                    onItemClick(notification)
                }

                deleteButton.setOnClickListener {
                    onDelete(notification.id)
                }

                markReadButton.setOnClickListener {
                    onMarkRead(notification.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class NotificationDiffCallback : DiffUtil.ItemCallback<Notification>() {
    override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem == newItem
    }
}
