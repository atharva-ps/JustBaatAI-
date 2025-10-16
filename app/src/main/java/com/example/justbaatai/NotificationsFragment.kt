package com.example.justbaatai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justbaatai.databinding.FragmentNotificationsBinding
import com.example.justbaatai.ui.NotificationsViewModel

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    // Get a reference to the ViewModel
    private val viewModel: NotificationsViewModel by viewModels()
    private lateinit var notificationsAdapter: NotificationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        // Observe the data from the ViewModel
        viewModel.notifications.observe(viewLifecycleOwner) { notificationList ->
            // When the data changes, submit the new list to the adapter
            notificationsAdapter.submitList(notificationList)
            // Show the "empty" message if the list is empty
            binding.emptyStateTextView.isVisible = notificationList.isEmpty()
        }

        // The button now calls the ViewModel to clear the data
        binding.clearButton.setOnClickListener {
            viewModel.clearNotifications()
        }

        // Tell the ViewModel to load the initial data
        viewModel.loadNotifications()
    }

    private fun setupRecyclerView() {
        // Create the adapter with an empty constructor
        notificationsAdapter = NotificationsAdapter()
        binding.notificationsRecyclerView.apply {
            adapter = notificationsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}