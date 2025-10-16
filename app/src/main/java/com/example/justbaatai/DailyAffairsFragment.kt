package com.example.justbaatai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.justbaatai.databinding.FragmentDailyAffairsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DailyAffairsFragment : Fragment() {

    private var _binding: FragmentDailyAffairsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyAffairsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}