package com.justbaat.mindoro.catnotification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.justbaat.mindoro.R

class NotificationsViewModel : ViewModel() {

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> = _notifications

    private val _unreadCount = MutableLiveData<Int>()
    val unreadCount: LiveData<Int> = _unreadCount

    fun loadNotifications() {
        viewModelScope.launch {
            // In real app, fetch from database or API
            val sampleNotifications = listOf(
                Notification(
                    id = "notif_1",
                    title = "🎉 New Test Available",
                    message = "SSC CGL Mock Test #25 is now available. Test your preparation level.",
                    timestamp = System.currentTimeMillis() - 300000, // 5 mins ago
                    type = NotificationType.TEST,
                    icon = "ic_test_new",
                    isRead = false,
                    actionUrl = "nav_free_tests"
                ),
                Notification(
                    id = "notif_2",
                    title = "📚 Study Material Updated",
                    message = "Chapter 5: Modern History notes have been updated with latest questions.",
                    timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
                    type = NotificationType.STUDY,
                    icon = "ic_study_material",
                    isRead = false,
                    actionUrl = "nav_study_notes"
                ),
                Notification(
                    id = "notif_3",
                    title = "⭐ Achievement Unlocked",
                    message = "You've completed 10 tests! Keep up the momentum.",
                    timestamp = System.currentTimeMillis() - 7200000, // 2 hours ago
                    type = NotificationType.ACHIEVEMENT,
                    icon = "ic_achievement",
                    isRead = true,
                    actionUrl = ""
                ),
                Notification(
                    id = "notif_4",
                    title = "🎓 New Course: UPSC Prelims",
                    message = "Master UPSC Prelims with our comprehensive course. Limited time offer.",
                    timestamp = System.currentTimeMillis() - 86400000, // 1 day ago
                    type = NotificationType.COURSE,
                    icon = "ic_course",
                    isRead = true,
                    actionUrl = "nav_courses"
                ),
                Notification(
                    id = "notif_5",
                    title = "⏰ Daily Reminder",
                    message = "Complete your daily quiz challenge to maintain your streak!",
                    timestamp = System.currentTimeMillis() - 172800000, // 2 days ago
                    type = NotificationType.REMINDER,
                    icon = "ic_reminder",
                    isRead = true,
                    actionUrl = "nav_quizzes"
                ),
                Notification(
                    id = "notif_6",
                    title = "🎁 Special Offer",
                    message = "Get 50% off on all courses. Use code: STUDY50",
                    timestamp = System.currentTimeMillis() - 259200000, // 3 days ago
                    type = NotificationType.OFFER,
                    icon = "ic_offer",
                    isRead = true,
                    actionUrl = "nav_courses"
                )
            )

            _notifications.value = sampleNotifications
            updateUnreadCount(sampleNotifications)
        }
    }

    fun markAsRead(notificationId: String) {
        val updatedList = _notifications.value?.map { notification ->
            if (notification.id == notificationId) {
                notification.copy(isRead = true)
            } else {
                notification
            }
        }
        _notifications.value = updatedList ?: emptyList()
        updatedList?.let { updateUnreadCount(it) }
    }

    fun markAllAsRead() {
        val updatedList = _notifications.value?.map { it.copy(isRead = true) }
        _notifications.value = updatedList ?: emptyList()
        updateUnreadCount(updatedList ?: emptyList())
    }

    fun deleteNotification(notificationId: String) {
        val updatedList = _notifications.value?.filter { it.id != notificationId }
        _notifications.value = updatedList ?: emptyList()
        updatedList?.let { updateUnreadCount(it) }
    }

    fun clearNotifications() {
        _notifications.value = emptyList()
        _unreadCount.value = 0
    }

    private fun updateUnreadCount(notifications: List<Notification>) {
        _unreadCount.value = notifications.count { !it.isRead }
    }
}
