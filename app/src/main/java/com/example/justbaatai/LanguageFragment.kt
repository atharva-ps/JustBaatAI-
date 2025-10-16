package com.example.justbaatai

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justbaatai.Language
import com.example.justbaatai.LanguageAdapter
import com.example.justbaatai.databinding.FragmentLanguageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageFragment : Fragment() {

    private var _binding: FragmentLanguageBinding? = null
    private val binding get() = _binding!!

    private lateinit var languageAdapter: LanguageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.saveButton.setOnClickListener {
            // FIX #2: Call the 'getSelectedLanguage()' function from the adapter
            val selectedLanguage = languageAdapter.getSelectedLanguage()

            if (selectedLanguage != null) {
                Toast.makeText(
                    requireContext(),
                    "Saved language: ${selectedLanguage.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupRecyclerView() {
        // Create the sample data list
        val languages = listOf(
            Language("English", "en"),
            Language("हिन्दी (Hindi)", "hi"),
            Language("Español (Spanish)", "es")
        )

        // FIX #1: Create the adapter with an EMPTY constructor
        languageAdapter = LanguageAdapter()

        binding.languageRecyclerView.apply {
            adapter = languageAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // And pass the list to the adapter using .submitList()
        languageAdapter.submitList(languages)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}