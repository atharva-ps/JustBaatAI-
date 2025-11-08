package com.justbaat.mindoro.catcurrentaffairs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.justbaat.mindoro.databinding.ActivityCategoryCoursesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryCoursesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryCoursesBinding
    private lateinit var repository: CurrentAffairsRepository
    private lateinit var coursesAdapter: CoursesAdapter
    private var categoryId: String = ""
    private var allCourses: List<Course> = emptyList()

    companion object {
        private const val EXTRA_CATEGORY_ID = "category_id"
        private const val EXTRA_CATEGORY_TITLE = "category_title"

        fun start(context: Context, categoryId: String, categoryTitle: String) {
            val intent = Intent(context, CategoryCoursesActivity::class.java).apply {
                putExtra(EXTRA_CATEGORY_ID, categoryId)
                putExtra(EXTRA_CATEGORY_TITLE, categoryTitle)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryCoursesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoryId = intent.getStringExtra(EXTRA_CATEGORY_ID) ?: ""
        val categoryTitle = intent.getStringExtra(EXTRA_CATEGORY_TITLE) ?: ""

        repository = CurrentAffairsRepository(this)

        setupToolbar(categoryTitle)
        setupRecyclerView()
        setupSearch()
        loadCourses()
    }

    private fun setupToolbar(title: String) {
        binding.tvCategoryTitle.text = "$title Passes"
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        // Initialize adapter once
        coursesAdapter = CoursesAdapter { course ->
            handleCourseClick(course)
        }

        binding.rvCourses.apply {
            layoutManager = LinearLayoutManager(this@CategoryCoursesActivity)
            adapter = coursesAdapter
            itemAnimator = null // Disable animations to prevent flickering
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterCourses(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadCourses() {
        val category = repository.getCategoryById(categoryId)
        allCourses = category?.courses ?: emptyList()

        // Submit initial list
        coursesAdapter.submitList(allCourses.toList())
    }

    private fun filterCourses(query: String) {
        val filteredCourses = if (query.isEmpty()) {
            allCourses
        } else {
            allCourses.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.features.any { feature -> feature.contains(query, ignoreCase = true) }
            }
        }

        // Update the existing adapter instead of creating a new one
        coursesAdapter.submitList(filteredCourses.toList())
    }

    private fun handleCourseClick(course: Course) {
        if (course.url.isNotEmpty()) {
            try {
                // Open URL in browser
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(course.url))
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Cannot open URL: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No URL available for this course", Toast.LENGTH_SHORT).show()
        }
    }
}