package com.example.justbaatai.catfreequizzes


import android.content.Context
import com.example.justbaatai.R
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.InputStreamReader

data class QuizDataResponse(
    @SerializedName("categories")
    val categories: List<QuizCategory>
)

class QuizRepository(private val context: Context) {

    private var cachedData: QuizDataResponse? = null

    // LOCAL DATA SOURCE (Current)
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

    // BACKEND API (Future - just uncomment and add Retrofit)
    /*
    private suspend fun loadFromBackend(): QuizDataResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.getQuizCategories()
            if (response.isSuccessful) {
                response.body() ?: QuizDataResponse(emptyList())
            } else {
                throw Exception("Failed to load quiz data")
            }
        }
    }
    */

    // UNIFIED METHOD - Switch between local/backend easily
    fun loadQuizData(): QuizDataResponse {
        // TODO: When backend is ready, uncomment this:
        // return runBlocking { loadFromBackend() }

        // Current: Load from local JSON
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

    // Get all tests from all categories
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

    // Search tests by query
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
