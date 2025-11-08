package com.justbaat.mindoro.catfreetests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.justbaat.mindoro.databinding.FragmentFreeTestsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TopFreeTestsFragment : Fragment() {
    private var _binding: FragmentFreeTestsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFreeTestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}