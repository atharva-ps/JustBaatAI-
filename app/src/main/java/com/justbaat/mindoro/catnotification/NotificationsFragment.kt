package com.justbaat.mindoro.catnotification

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.justbaat.mindoro.R
import com.justbaat.mindoro.databinding.FragmentNotificationsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NotificationsViewModel by viewModels()
    private lateinit var notificationsAdapter: NotificationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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
        setupUI()

        // Observe notifications
        viewModel.notifications.observe(viewLifecycleOwner) { notificationList ->
            notificationsAdapter.submitList(notificationList)
            binding.emptyStateTextView.isVisible = notificationList.isEmpty()
            binding.notificationsRecyclerView.isVisible = notificationList.isNotEmpty()
        }

        // Observe unread count
        viewModel.unreadCount.observe(viewLifecycleOwner) { count ->
            binding.unreadCountBadge.isVisible = count > 0
            binding.unreadCountBadge.text = if (count > 99) "99+" else count.toString()
        }

        viewModel.loadNotifications()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_notifications, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_mark_all_read -> {
                viewModel.markAllAsRead()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        notificationsAdapter = NotificationsAdapter(
            onItemClick = { notification ->
                // Navigate based on notification type
                if (notification.actionUrl.isNotEmpty()) {
                    // TODO: Navigate to the action URL
                    // findNavController().navigate(notification.actionUrl)
                }
            },
            onMarkRead = { notificationId ->
                viewModel.markAsRead(notificationId)
            },
            onDelete = { notificationId ->
                viewModel.deleteNotification(notificationId)
            }
        )

        binding.notificationsRecyclerView.apply {
            adapter = notificationsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupUI() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.clearButton.setOnClickListener {
            viewModel.clearNotifications()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
