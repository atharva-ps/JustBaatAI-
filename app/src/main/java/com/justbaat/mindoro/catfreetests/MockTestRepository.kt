package com.justbaat.mindoro.catfreetests


import android.content.Context
import com.justbaat.mindoro.R
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.InputStreamReader

data class MockTestDataResponse(
    @SerializedName("categories")
    val categories: List<MockTestCategory>
)

class MockTestRepository(private val context: Context) {

    private var cachedData: MockTestDataResponse? = null

    // LOCAL DATA SOURCE (Current)
    private fun loadFromLocalJson(): MockTestDataResponse {
        if (cachedData != null) {
            return cachedData!!
        }

        val inputStream = context.resources.openRawResource(R.raw.mock_tests_data)
        val reader = InputStreamReader(inputStream)
        cachedData = Gson().fromJson(reader, MockTestDataResponse::class.java)
        reader.close()

        return cachedData!!
    }


    // UNIFIED METHOD - Easy to switch
    fun loadMockTests(): MockTestDataResponse {
        // TODO: When backend is ready, switch to:
        // return runBlocking { loadFromBackend() }
        return loadFromLocalJson()
    }

    // Get all categories
    fun getAllCategories(): List<MockTestCategory> {
        return loadMockTests().categories
    }

    // Get category by ID
    fun getCategoryById(categoryId: String): MockTestCategory? {
        return loadMockTests().categories.find { it.id == categoryId }
    }

    // Get all tests
    fun getAllTests(): List<MockTest> {
        return loadMockTests().categories.flatMap { it.tests }
    }

    // Get tests by category
    fun getTestsByCategory(categoryId: String): List<MockTest> {
        return getCategoryById(categoryId)?.tests ?: emptyList()
    }

    // Get test by ID
    fun getTestById(testId: String): MockTest? {
        return getAllTests().find { it.id == testId }
    }

    // Search tests
    fun searchTests(query: String): List<MockTest> {
        return getAllTests().filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true) ||
                    it.topics.any { topic -> topic.contains(query, ignoreCase = true) }
        }
    }
}
