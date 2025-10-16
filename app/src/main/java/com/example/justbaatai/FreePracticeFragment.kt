package com.example.justbaatai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.justbaatai.databinding.FragmentFreePracticeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FreePracticeFragment : Fragment() {

    private var _binding: FragmentFreePracticeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFreePracticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}