package com.justbaat.mindoro.catfreequizzes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.justbaat.mindoro.databinding.FragmentFreeQuizzesBinding
import com.justbaat.mindoro.workers.WorkManagerInitializer
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class FreeQuizzesFragment : Fragment() {

    private var _binding: FragmentFreeQuizzesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FreeQuizzesViewModel by viewModels()

    private lateinit var categoryAdapter: QuizCategoryAdapter
    private lateinit var testsAdapter: LiveTestsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFreeQuizzesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSwipeRefresh()
        setupCategories()
        setupTests()
        observeViewModel()
        updateLastSyncTime()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.syncQuizData()
        }
    }

    private fun setupCategories() {
        categoryAdapter = QuizCategoryAdapter(emptyList()) { category ->
            viewModel.selectCategory(category.id)
        }

        binding.rvQuizCategories.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
    }

    private fun setupTests() {
        testsAdapter = LiveTestsAdapter(emptyList()) { test ->
            handleTestClick(test)
        }

        binding.rvLiveTests.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = testsAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.updateCategories(categories)
        }

        viewModel.filteredTests.observe(viewLifecycleOwner) { tests ->
            testsAdapter.updateTests(tests)

            when {
                tests.isEmpty() -> {
                    binding.placeholderState.isVisible = false
                    binding.emptyState.isVisible = true
                    binding.rvLiveTests.isVisible = false
                }
                else -> {
                    binding.placeholderState.isVisible = false
                    binding.emptyState.isVisible = false
                    binding.rvLiveTests.isVisible = true
                }
            }
        }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is QuizUiState.Loading -> showLoading()
                is QuizUiState.Success -> hideLoading()
                is QuizUiState.Error -> showError(state.message)
            }
        }

        viewModel.syncStatus.observe(viewLifecycleOwner) { status ->
            binding.swipeRefresh.isRefreshing = false

            when (status) {
                is SyncStatus.Syncing -> {
                    // Already handled by swipeRefresh
                }
                is SyncStatus.Success -> {
                    Toast.makeText(context, "Data synced successfully!", Toast.LENGTH_SHORT).show()
                    updateLastSyncTime()
                }
                is SyncStatus.Failed -> {
                    Toast.makeText(context, "Sync failed: ${status.message}", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun updateLastSyncTime() {
        val lastSync = viewModel.getLastSyncTime()
        if (lastSync > 0) {
            val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
            val lastSyncText = "Last synced: ${dateFormat.format(Date(lastSync))}"
            binding.tvLastSync?.text = lastSyncText
        }
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
        binding.contentLayout.isVisible = false
    }

    private fun hideLoading() {
        binding.progressBar.isVisible = false
        binding.contentLayout.isVisible = true
    }

    private fun showError(message: String) {
        binding.progressBar.isVisible = false
        binding.errorLayout.isVisible = true
        binding.errorMessage.text = message
        binding.retryButton.setOnClickListener {
            binding.errorLayout.isVisible = false
            viewModel.retry()
        }
    }

    private fun handleTestClick(test: LiveTest) {
        if (test.status == "Start Now") {
            val questions = test.questions

            if (questions.isNotEmpty()) {
                QuizActivity.start(requireContext(), test, questions)
            } else {
                Toast.makeText(context, "No questions available for this test", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Register for: ${test.title}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
