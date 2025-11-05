package com.example.justbaatai.catfreetests

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justbaatai.catfreetests.MockTest
import com.example.justbaatai.catfreetests.MockTestRepository
import com.example.justbaatai.catfreetests.MockTestsAdapter
import com.example.justbaatai.catfreetests.TestActivity
import com.example.justbaatai.databinding.FragmentFreeTestsBinding
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FreeTestsFragment : Fragment() {

    private var _binding: FragmentFreeTestsBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: MockTestRepository
    private lateinit var testsAdapter: MockTestsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFreeTestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = MockTestRepository(requireContext())

        setupTabs()
        loadTests("cat_recommended") // Load recommended tests by default
    }

    private fun setupTabs() {
        val categories = repository.getAllCategories()

        // Add tabs for each category
        categories.forEach { category ->
            binding.tabLayout.addTab(
                binding.tabLayout.newTab().setText(category.name)
            )
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val category = categories[it.position]
                    loadTests(category.id)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadTests(categoryId: String) {
        val tests = repository.getTestsByCategory(categoryId)

        testsAdapter = MockTestsAdapter(tests) { test ->
            startTest(test)
        }

        binding.testsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = testsAdapter
        }
    }

    private fun startTest(test: MockTest) {
        // Navigate to instructions screen instead of directly to test
        TestInstructionsActivity.start(requireContext(), test.id)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
