package com.justbaat.mindoro.catfreequizzes

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.justbaat.mindoro.R
import com.google.gson.Gson
import com.google.gson.JsonParser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var cachedData: QuizDataResponse? = null
    private val prefs: SharedPreferences = context.getSharedPreferences("quiz_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val TAG = "QuizRepository"
        private const val CACHED_QUIZ_DATA = "cached_quiz_data"
        private const val LAST_SYNC_TIME = "last_sync_time"

        // ⚠️ REPLACE WITH YOUR GOOGLE SHEET ID
        private const val SHEET_ID = "1m4aZ2iAiC0YyZPoY8vprTX2oS9ON4pPRp-wdLuEyCPo"
        private const val GOOGLE_SHEETS_URL =
            "https://docs.google.com/spreadsheets/d/$SHEET_ID/gviz/tq?tqx=out:json&sheet=Sheet1"
    }

    // Load from cache or local JSON fallback
    private fun loadFromLocalJson(): QuizDataResponse {
        // Try to load from cache first
        val cachedJson = prefs.getString(CACHED_QUIZ_DATA, null)
        if (cachedJson != null) {
            try {
                Log.d(TAG, "✅ LOADING FROM CACHE (Google Sheets data)")
                Log.d(TAG, "Cache size: ${cachedJson.length} characters")
                return gson.fromJson(cachedJson, QuizDataResponse::class.java)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Cache corrupted, falling back to local JSON", e)
            }
        }

        // Fallback to local JSON
        Log.d(TAG, "📄 LOADING FROM LOCAL JSON FILE (res/raw/quizzes_data.json)")
        val inputStream = context.resources.openRawResource(R.raw.quizzes_data)
        val reader = InputStreamReader(inputStream)
        val data = gson.fromJson(reader, QuizDataResponse::class.java)
        reader.close()

        return data
    }

    suspend fun syncQuizDataFromServer(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🌐 FETCHING DATA FROM GOOGLE SHEETS...")
            Log.d(TAG, "URL: $GOOGLE_SHEETS_URL")

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                .url(GOOGLE_SHEETS_URL)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.e(TAG, "❌ Sync failed with code: ${response.code}")
                return@withContext false
            }

            val responseBody = response.body?.string() ?: run {
                Log.e(TAG, "❌ Empty response body")
                return@withContext false
            }

            Log.d(TAG, "✅ Response received, length: ${responseBody.length}")

            // Parse Google Sheets response
            val jsonString = extractJsonFromGoogleSheets(responseBody)
            val quizData = parseGoogleSheetsToQuizData(jsonString)

            if (quizData.categories.isEmpty()) {
                Log.e(TAG, "❌ Parsed data is empty")
                return@withContext false
            }

            Log.d(TAG, "✅ Parsed ${quizData.categories.size} categories")

            // Save to cache
            val jsonData = gson.toJson(quizData)
            prefs.edit()
                .putString(CACHED_QUIZ_DATA, jsonData)
                .putLong(LAST_SYNC_TIME, System.currentTimeMillis())
                .apply()

            cachedData = quizData
            Log.d(TAG, "✅ GOOGLE SHEETS DATA CACHED SUCCESSFULLY")

            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error syncing from Google Sheets", e)
            false
        }
    }


    private fun extractJsonFromGoogleSheets(response: String): String {
        // Google Sheets returns: google.visualization.Query.setResponse({...});
        val startIndex = response.indexOf("{")
        val endIndex = response.lastIndexOf("}")
        return if (startIndex != -1 && endIndex != -1) {
            response.substring(startIndex, endIndex + 1)
        } else {
            "{}"
        }
    }

    private fun parseGoogleSheetsToQuizData(jsonString: String): QuizDataResponse {
        try {
            val jsonObject = JsonParser.parseString(jsonString).asJsonObject
            val table = jsonObject.getAsJsonObject("table")
            val rows = table.getAsJsonArray("rows")

            Log.d(TAG, "📊 Total rows in sheet: ${rows.size()}")

            val testsMap = mutableMapOf<String, MutableList<QuizQuestion>>()
            val testMetadata = mutableMapOf<String, TestInfo>()

            var questionCounter = 1
            var processedRows = 0
            var skippedRows = 0

            rows.forEachIndexed { index, row ->
                try {
                    val cells = row.asJsonObject.getAsJsonArray("c")

                    // Skip if not enough cells
                    if (cells == null || cells.size() < 9) {
                        Log.w(TAG, "⚠️ Row ${index + 1}: Insufficient cells, skipping")
                        skippedRows++
                        return@forEachIndexed
                    }

                    // 🔑 KEY FIX: Use ?.asJsonObject to handle null cells
                    fun getCellValue(index: Int): String {
                        return try {
                            cells[index]?.asJsonObject?.get("v")?.asString?.trim() ?: ""
                        } catch (e: Exception) {
                            ""
                        }
                    }

                    fun getCellNumber(index: Int): Int {
                        return try {
                            val cell = cells[index]?.asJsonObject?.get("v")
                            when {
                                cell?.isJsonPrimitive == true && cell.asJsonPrimitive.isNumber ->
                                    cell.asJsonPrimitive.asInt
                                cell?.isJsonPrimitive == true && cell.asJsonPrimitive.isString ->
                                    cell.asJsonPrimitive.asString.trim().toIntOrNull() ?: 1
                                else -> 1
                            }
                        } catch (e: Exception) {
                            1
                        }
                    }

                    // 📝 EXTRACT DATA with null safety
                    val exam = getCellValue(0)
                    val subject = getCellValue(1)
//                    val testSeriesName = getCellValue(2)
                    val question = getCellValue(2)
                    val option1 = getCellValue(3)
                    val option2 = getCellValue(4)
                    val option3 = getCellValue(5)
                    val option4 = getCellValue(6)
                    val correctAnswerFromSheet = getCellNumber(7)

                    // ⚠️ VALIDATION: Skip invalid rows
                    if (exam.isEmpty() ||
                        exam.equals("exam", ignoreCase = true) ||
                        subject.isEmpty() ||
                        subject.equals("subject", ignoreCase = true) ||
                        question.isEmpty() ||
                        question.equals("question", ignoreCase = true) ||
                        option1.isEmpty() ||
                        option2.isEmpty()) {

                        Log.w(TAG, "⚠️ Row ${index + 1}: Invalid/empty data (exam='$exam', subject='$subject', question='${question.take(20)}'), skipping")
                        skippedRows++
                        return@forEachIndexed
                    }

                    // ✅ Convert from 1-based (Sheet) to 0-based (App)
                    val correctAnswer = (correctAnswerFromSheet - 1).coerceIn(0, 3)

                    // 🆔 GENERATE IDs
                    val categoryId = "cat_${exam.lowercase().replace(" ", "_")}"
                    val testId = "${categoryId}_${subject.lowercase().replace(" ", "_")}"
                    val questionId = "q${questionCounter}_${testId}"
                    questionCounter++
                    processedRows++

                    // 🔍 DEBUG LOG
                    Log.d(TAG, """
                    ✅ Row ${index + 1} PROCESSED:
                    Exam: "$exam" → Category: $categoryId
                    Subject: "$subject" → Test: $testId
                    Question: ${question.take(40)}...
                    Options: [1:$option1, 2:$option2, 3:$option3, 4:$option4]
                    Correct (Sheet): $correctAnswerFromSheet → (App): $correctAnswer
                    ✅ Correct Answer: ${listOf(option1, option2, option3, option4)[correctAnswer]}
                """.trimIndent())

                    // 📦 STORE TEST METADATA
                    if (!testMetadata.containsKey(testId)) {
                        testMetadata[testId] = TestInfo(
                            id = testId,
                            title = "$exam - $subject",
                            subject = subject,
                            categoryId = categoryId,
                            categoryName = exam
//                            testSeriesName = testSeriesName
                        )
                        Log.d(TAG, "📝 New test created: $testId ($exam - $subject)")
                    }

                    // 📝 CREATE QUIZ QUESTION
                    val quizQuestion = QuizQuestion(
                        id = questionId,
                        question = question,
                        options = listOf(option1, option2, option3, option4),
                        correctAnswer = correctAnswer,
                        explanation = "Correct answer is: ${listOf(option1, option2, option3, option4)[correctAnswer]}",
                        marks = 1.0,
                        negativeMarks = 0.25
                    )

                    testsMap.getOrPut(testId) { mutableListOf() }.add(quizQuestion)

                } catch (e: Exception) {
                    Log.e(TAG, "❌ Error parsing row ${index + 1}: ${e.message}", e)
                    skippedRows++
                }
            }

            Log.d(TAG, """
            
            📊 PARSING SUMMARY:
            Total Rows: ${rows.size()}
            ✅ Processed: $processedRows
            ⚠️ Skipped: $skippedRows
            📝 Tests Created: ${testMetadata.size}
            
        """.trimIndent())

            // 🏗️ BUILD FINAL STRUCTURE (same as before)
            val categoriesMap = mutableMapOf<String, MutableList<LiveTest>>()
            val categoryNames = mutableMapOf<String, String>()

            testMetadata.forEach { (testId, testInfo) ->
                val questions = testsMap[testId] ?: emptyList()

                if (questions.isEmpty()) {
                    Log.w(TAG, "⚠️ Test $testId has no questions, skipping")
                    return@forEach
                }

                categoryNames[testInfo.categoryId] = testInfo.categoryName

                Log.d(TAG, "📚 Creating Test: ${testInfo.title} with ${questions.size} questions")

                val liveTest = LiveTest(
                    id = testId,
                    title = testInfo.title,
                    isFree = true,
                    isLiveTest = true,
                    tags = listOf(testInfo.categoryName, testInfo.subject),
                    questionsCount = questions.size,
                    durationMinutes = questions.size,
                    totalMarks = questions.size.toDouble(),
                    languages = listOf("English", "Hindi"),
                    endsInDays = 7,
                    status = "Start Now",
                    examType = testInfo.categoryName,
                    categoryId = testInfo.categoryId,
                    questions = questions
                )

                categoriesMap.getOrPut(testInfo.categoryId) { mutableListOf() }.add(liveTest)
            }

            // 📊 CREATE CATEGORIES
            val categories = categoriesMap.map { (categoryId, tests) ->
                val categoryName = categoryNames[categoryId] ?: categoryId
                val totalQuestions = tests.sumOf { it.questionsCount }

                Log.d(TAG, """
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                📁 Category: $categoryName
                Tests: ${tests.size}
                ${tests.joinToString("\n") { "  → ${it.title}: ${it.questionsCount} questions" }}
                Total Questions: $totalQuestions
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            """.trimIndent())

                QuizCategory(
                    id = categoryId,
                    categoryName = categoryName,
                    testsCount = tests.size,
                    quizzesCount = totalQuestions,
                    icon = categoryId.replace("cat_", ""),
                    tests = tests
                )
            }.sortedBy { it.categoryName }

            Log.d(TAG, """
            
            ✅ ━━━━━━━━━━━━━ PARSING COMPLETE ━━━━━━━━━━━━━
            Total Categories: ${categories.size}
            Total Tests: ${testMetadata.size}
            Total Questions: ${testsMap.values.sumOf { it.size }}
            
            Final Summary:
            ${categories.joinToString("\n") {
                "  📁 ${it.categoryName}: ${it.testsCount} tests, ${it.quizzesCount} questions"
            }}
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        """.trimIndent())

            return QuizDataResponse(categories = categories)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error parsing Google Sheets data", e)
            return QuizDataResponse(categories = emptyList())
        }
    }

    // Helper data class (same as before)
    private data class TestInfo(
        val id: String,
        val title: String,
        val subject: String,
        val categoryId: String,
        val categoryName: String,
//        val testSeriesName: String
    )


    // Helper data classes for parsing
    private data class CategoryMetadata(
        val id: String,
        val name: String,
        val testsCount: Int,
        val quizzesCount: Int
    )

    private data class TestMetadata(
        val id: String,
        val title: String,
        val isFree: Boolean,
        val isLiveTest: Boolean,
        val tags: List<String>,
        val questionsCount: Int,
        val durationMinutes: Int,
        val totalMarks: Double,
        val languages: List<String>,
        val endsInDays: Int,
        val status: String,
        val examType: String,
        val categoryId: String
    )

    fun loadQuizData(): QuizDataResponse {
        if (cachedData != null) {
            return cachedData!!
        }

        cachedData = loadFromLocalJson()
        return cachedData!!
    }

    fun getLastSyncTime(): Long {
        return prefs.getLong(LAST_SYNC_TIME, 0)
    }

    fun clearCache() {
        cachedData = null
        prefs.edit().clear().apply()
    }

    // Existing methods remain the same
    fun getAllCategories(): List<QuizCategory> = loadQuizData().categories

    fun getCategoryById(categoryId: String): QuizCategory? =
        loadQuizData().categories.find { it.id == categoryId }

    fun getAllLiveTests(): List<LiveTest> =
        loadQuizData().categories.flatMap { it.tests }

    fun getTestsByCategory(categoryId: String): List<LiveTest> =
        loadQuizData().categories.find { it.id == categoryId }?.tests ?: emptyList()

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

    fun searchTests(query: String): List<LiveTest> =
        getAllLiveTests().filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.tags.any { tag -> tag.contains(query, ignoreCase = true) } ||
                    it.examType.contains(query, ignoreCase = true)
        }

    fun getTestById(testId: String): LiveTest? =
        getAllLiveTests().find { it.id == testId }
}
