package com.justbaat.mindoro.catfreequizzes

import android.content.Context
import com.justbaat.mindoro.R
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var cachedData: QuizDataResponse? = null

    // Load from local JSON
    private fun loadFromLocalJson(): QuizDataResponse {
        if (cachedData != null) {
            return cachedData!!
        }

        val inputStream = context.resources.openRawResource(R.raw.quizzes_data)
        val reader = InputStreamReader(inputStream)
        cachedData = Gson().fromJson(reader, QuizDataResponse::class.java)
        reader.close()

        return cachedData!!
    }

    // Unified method
    fun loadQuizData(): QuizDataResponse {
        return loadFromLocalJson()
    }

    // Get all categories
    fun getAllCategories(): List<QuizCategory> {
        return loadQuizData().categories
    }

    // Get category by ID
    fun getCategoryById(categoryId: String): QuizCategory? {
        return loadQuizData().categories.find { it.id == categoryId }
    }

    // Get all tests
    fun getAllLiveTests(): List<LiveTest> {
        return loadQuizData().categories.flatMap { it.tests }
    }

    // Get tests by category
    fun getTestsByCategory(categoryId: String): List<LiveTest> {
        return loadQuizData().categories
            .find { it.id == categoryId }
            ?.tests ?: emptyList()
    }

    // Get filtered tests
    fun getFilteredTests(filterType: String, categoryId: String? = null): List<LiveTest> {
        val tests = if (categoryId != null) {
            getTestsByCategory(categoryId)
        } else {
            getAllLiveTests()
        }

        return when (filterType) {
            "free" -> tests.filter { it.isFree }
            "live" -> tests.filter { it.isLiveTest }
            "premium" -> tests.filter { !it.isFree }
            else -> tests
        }
    }

    // Search tests
    fun searchTests(query: String): List<LiveTest> {
        return getAllLiveTests().filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.tags.any { tag -> tag.contains(query, ignoreCase = true) } ||
                    it.examType.contains(query, ignoreCase = true)
        }
    }

    // Get test by ID
    fun getTestById(testId: String): LiveTest? {
        return getAllLiveTests().find { it.id == testId }
    }
}
