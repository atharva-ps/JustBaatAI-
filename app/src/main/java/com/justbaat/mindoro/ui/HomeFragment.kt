package com.justbaat.mindoro.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.justbaat.mindoro.R
import com.justbaat.mindoro.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var homeGridAdapter: HomeGridAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        setupSearch()

        viewModel.gridItems.observe(viewLifecycleOwner) { items ->
            (binding.homeRecyclerview.adapter as? HomeGridAdapter)?.submitList(items)
        }

        viewModel.loadInitialData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifications -> {
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

        // Quick action buttons
        binding.quizzesButton.setOnClickListener {
            findNavController().navigate(R.id.nav_quizzes)
        }
        binding.testsButton.setOnClickListener {
            findNavController().navigate(R.id.nav_top_free_tests)
        }
        binding.notesButton.setOnClickListener {
            findNavController().navigate(R.id.nav_notes)
        }
        binding.coursesButton.setOnClickListener {
            findNavController().navigate(R.id.nav_courses)
        }

        // View All button
//        binding.viewAllText.setOnClickListener {
//            // TODO: Navigate to all categories screen
//        }
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO: Implement search functionality
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
