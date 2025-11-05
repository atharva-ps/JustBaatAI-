package com.example.justbaatai.catpreviousyear

import android.content.Context
import com.example.justbaatai.R
import com.google.gson.Gson
import java.io.InputStreamReader

class ExamRepository(private val context: Context) {

    private var cachedData: ExamDataResponse? = null

    // Load from JSON file
    fun loadExamData(): ExamDataResponse {
        if (cachedData != null) {
            return cachedData!!
        }

        val inputStream = context.resources.openRawResource(R.raw.exam_papers_data)
        val reader = InputStreamReader(inputStream)
        cachedData = Gson().fromJson(reader, ExamDataResponse::class.java)
        reader.close()

        return cachedData!!
    }

    // Get all categories
    fun getAllCategories(): List<Category> {
        return loadExamData().categories
    }

    // Get exams for a specific category
    fun getExamsForCategory(categoryName: String): List<ExamData> {
        return loadExamData().categories
            .find { it.categoryName.equals(categoryName, ignoreCase = true) }
            ?.exams ?: emptyList()
    }

    // Get papers for a specific exam
    fun getPapersForExam(examName: String): List<YearGroup> {
        loadExamData().categories.forEach { category ->
            category.exams.forEach { exam ->
                if (exam.examName.equals(examName, ignoreCase = true)) {
                    return exam.yearGroups.toMutableList()
                }
            }
        }
        return emptyList()
    }

    // For future backend integration
    suspend fun loadExamDataFromBackend(): ExamDataResponse {
        // TODO: Replace with actual API call
        // Example:
        // val response = apiService.getExamData()
        // return response

        // For now, return local data
        return loadExamData()
    }
}
