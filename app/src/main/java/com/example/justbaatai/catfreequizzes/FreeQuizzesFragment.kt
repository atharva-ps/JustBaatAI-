package com.example.justbaatai.catfreequizzes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justbaatai.*
import com.example.justbaatai.databinding.FragmentFreeQuizzesBinding
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FreeQuizzesFragment : Fragment() {

    private var _binding: FragmentFreeQuizzesBinding? = null
    private val binding get() = _binding!!

    private lateinit var quizRepository: QuizRepository
    private lateinit var categoryAdapter: QuizCategoryAdapter
    private lateinit var testsAdapter: LiveTestsAdapter

    private var currentCategoryId: String? = null
    private var currentFilterType: String = "all"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFreeQuizzesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        quizRepository = QuizRepository(requireContext())

//        setupBackButton()
        setupTabs()
        setupCategories()
        setupFilterButtons()
        loadTests()
    }

//    private fun setupBackButton() {
//        binding.btnBack.setOnClickListener {
//            requireActivity().onBackPressed()
//        }
//    }

    private fun setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Ongoing"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Attempted"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> loadOngoingTests()
                    1 -> loadAttemptedTests()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupCategories() {
        val categories = quizRepository.getAllCategories()

        categoryAdapter = QuizCategoryAdapter(categories) { category ->
            currentCategoryId = category.id
            loadTests()
            Toast.makeText(context, "Showing: ${category.categoryName}", Toast.LENGTH_SHORT).show()
        }

        binding.rvQuizCategories.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
    }

    private fun setupFilterButtons() {
        binding.btnAllTests.setOnClickListener {
            binding.btnAllTests.setBackgroundResource(R.drawable.bg_button_selected)
            binding.btnFreeQuizzes.setBackgroundResource(R.drawable.bg_button_outlined_blue)
            currentFilterType = "all"
            loadTests()
        }

        binding.btnFreeQuizzes.setOnClickListener {
            binding.btnFreeQuizzes.setBackgroundResource(R.drawable.bg_button_selected)
            binding.btnAllTests.setBackgroundResource(R.drawable.bg_button_outlined_blue)
            currentFilterType = "free"
            loadTests()
        }
    }

    private fun loadTests() {
        val tests = quizRepository.getFilteredTests(currentFilterType, currentCategoryId)

        testsAdapter = LiveTestsAdapter(tests) { test ->
            handleTestClick(test)
        }

        binding.rvLiveTests.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = testsAdapter
        }
    }

    private fun loadOngoingTests() {
        loadTests()
    }

    private fun loadAttemptedTests() {
        // TODO: Load attempted tests from backend/database
        Toast.makeText(context, "Attempted tests - Coming soon", Toast.LENGTH_SHORT).show()
    }

    private fun handleTestClick(test: LiveTest) {
        if (test.status == "Start Now") {
            Toast.makeText(context, "Starting test: ${test.title}", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to test screen with test ID
            // findNavController().navigate(R.id.action_to_testScreen, Bundle().apply {
            //     putString("testId", test.id)
            // })
        } else {
            Toast.makeText(context, "Register for: ${test.title}", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to registration screen
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
