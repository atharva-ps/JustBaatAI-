package com.example.justbaatai.ui // Or your ViewModel package

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.justbaatai.catstudynotes.StudyNote

class NotificationsViewModel : ViewModel() {

    private val _notifications = MutableLiveData<List<String>>()
    val notifications: LiveData<List<String>> = _notifications

    fun loadNotifications() {
        // In a real app, you would load this from a database or network
        val sampleNotifications = listOf(
            "Welcome to the app!",
            "Your profile has been updated.",
            "New study notes are available for Chapter 3."
        )
        _notifications.value = sampleNotifications
    }

    fun clearNotifications() {
        // Clear the list by posting an empty list
        _notifications.value = emptyList()
    }
}