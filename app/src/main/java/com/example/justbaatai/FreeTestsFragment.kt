package com.example.justbaatai

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justbaatai.databinding.FragmentFreeTestsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FreeTestsFragment : Fragment() {

    private var _binding: FragmentFreeTestsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFreeTestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val testsAdapter = TestsAdapter()
        binding.testsRecyclerView.apply {
            adapter = testsAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // Create and submit some sample data
        val sampleTests = listOf(
            TestItem("test1", "Full Mock Test #1 - General Studies", 100, 60),
            TestItem("test2", "Subject Test - History", 50, 30),
            TestItem("test3", "Full Mock Test #2 - Aptitude", 120, 90)
        )
        testsAdapter.submitList(sampleTests)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}