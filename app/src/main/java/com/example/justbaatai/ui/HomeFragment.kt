package com.example.justbaatai.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.justbaatai.R
import com.example.justbaatai.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var homeGridAdapter: HomeGridAdapter

    // --- NEW: Step 1 ---
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This line tells the fragment that it has a menu to show
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()

        viewModel.gridItems.observe(viewLifecycleOwner) { items ->
            (binding.homeRecyclerview.adapter as? HomeGridAdapter)?.submitList(items)
        }

        viewModel.loadInitialData()
    }

    // --- NEW: Step 2 ---
    // This function inflates (creates) the menu in the toolbar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // --- NEW: Step 3 ---
    // This function handles clicks on the menu items
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifications -> {
                // Navigate to the notifications screen when the bell is clicked
                findNavController().navigate(R.id.nav_notifications)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupUI() {
        homeGridAdapter = HomeGridAdapter { gridItem ->
            findNavController().navigate(gridItem.actionId)
        }

        binding.homeRecyclerview.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = homeGridAdapter
        }

        binding.quizzesButton.setOnClickListener {
            findNavController().navigate(R.id.nav_quizzes)
        }
        binding.testsButton.setOnClickListener {
            findNavController().navigate(R.id.nav_top_free_tests)
        }
        binding.notesButton.setOnClickListener {
            findNavController().navigate(R.id.nav_notes)
        }
        binding.practiceButton.setOnClickListener {
            findNavController().navigate(R.id.nav_practice)
        }
        binding.coursesButton.setOnClickListener {
            findNavController().navigate(R.id.nav_courses)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}