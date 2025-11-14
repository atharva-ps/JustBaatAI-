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
import dagger.hilt.android.AndroidEntryPoint

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

        setupCategories()
        setupTests()
        observeViewModel()
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

            // Show appropriate state
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