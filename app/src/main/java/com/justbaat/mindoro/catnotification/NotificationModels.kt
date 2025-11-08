package com.justbaat.mindoro.catnotification

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Notification(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("timestamp")
    val timestamp: Long,
    @SerializedName("type")
    val type: NotificationType, // STUDY, TEST, COURSE, ACHIEVEMENT, GENERAL
    @SerializedName("icon")
    val icon: String = "", // drawable resource name
    @SerializedName("isRead")
    val isRead: Boolean = false,
    @SerializedName("actionUrl")
    val actionUrl: String = "", // Navigation route
    @SerializedName("imageUrl")
    val imageUrl: String = ""
) : Serializable

enum class NotificationType {
    STUDY,
    TEST,
    COURSE,
    ACHIEVEMENT,
    GENERAL,
    REMINDER,
    OFFER
}

fun getFormattedTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60000 -> "Just now"
        diff < 3600000 -> "${diff / 60000}m ago"
        diff < 86400000 -> "${diff / 3600000}h ago"
        diff < 604800000 -> "${diff / 86400000}d ago"
        else -> "${diff / 604800000}w ago"
    }
}
