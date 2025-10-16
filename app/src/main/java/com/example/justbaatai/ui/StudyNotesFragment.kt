package com.example.justbaatai.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justbaatai.StudyNotesAdapter
import com.example.justbaatai.databinding.FragmentStudyNotesBinding

class StudyNotesFragment : Fragment() {

    private var _binding: FragmentStudyNotesBinding? = null
    private val binding get() = _binding!!

    // This line will now be correct
    private val viewModel: StudyNotesViewModel by viewModels()
    private lateinit var studyNotesAdapter: StudyNotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudyNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewModel.studyNotes.observe(viewLifecycleOwner) { notes ->
            studyNotesAdapter.submitList(notes)
        }

        viewModel.loadStudyNotes()
    }

    private fun setupRecyclerView() {
        studyNotesAdapter = StudyNotesAdapter()
        binding.studyNotesRecyclerView.apply {
            adapter = studyNotesAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}