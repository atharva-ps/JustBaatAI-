package com.example.justbaatai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.justbaatai.databinding.FragmentStudyNotesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FreeNotesFragment : Fragment() {

    private var _binding: FragmentStudyNotesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudyNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}