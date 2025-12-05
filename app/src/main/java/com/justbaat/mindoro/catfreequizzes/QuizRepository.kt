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

            val testsMap = mutableMapOf<String, MutableList<QuizQuestion>>()
            val testMetadata = mutableMapOf<String, TestMetadata>()
            val categoryMetadata = mutableMapOf<String, CategoryMetadata>()

            rows.forEachIndexed { index, row ->
                try {
                    val cells = row.asJsonObject.getAsJsonArray("c")

                    if (cells.size() < 26) {
                        Log.w(TAG, "Row $index: Insufficient cells (${cells.size()})")
                        return@forEachIndexed
                    }

                    // Extract basic data
                    val testId = cells[0]?.asJsonObject?.get("v")?.asString ?: return@forEachIndexed
                    val categoryId = cells[1]?.asJsonObject?.get("v")?.asString ?: ""
                    val categoryName = cells[2]?.asJsonObject?.get("v")?.asString ?: ""
                    val testsCount = cells[3]?.asJsonObject?.get("v")?.asInt ?: 0
                    val quizzesCount = cells[4]?.asJsonObject?.get("v")?.asInt ?: 0
                    val title = cells[5]?.asJsonObject?.get("v")?.asString ?: ""
                    val isFree = cells[6]?.asJsonObject?.get("v")?.asString.equals("TRUE", true)
                    val isLiveTest = cells[7]?.asJsonObject?.get("v")?.asString.equals("TRUE", true)
                    val tags = cells[8]?.asJsonObject?.get("v")?.asString?.split(",")?.map { it.trim() } ?: listOf()
                    val questionsCount = cells[9]?.asJsonObject?.get("v")?.asInt ?: 5
                    val durationMinutes = cells[10]?.asJsonObject?.get("v")?.asInt ?: 5
                    val totalMarks = cells[11]?.asJsonObject?.get("v")?.asDouble ?: 5.0
                    val languages = cells[12]?.asJsonObject?.get("v")?.asString?.split(",")?.map { it.trim() } ?: listOf("English")
                    val endsInDays = cells[13]?.asJsonObject?.get("v")?.asInt ?: 5
                    val status = cells[14]?.asJsonObject?.get("v")?.asString ?: "Start Now"
                    val examType = cells[15]?.asJsonObject?.get("v")?.asString ?: ""

                    // Question data
                    val questionId = cells[16]?.asJsonObject?.get("v")?.asString ?: ""
                    val question = cells[17]?.asJsonObject?.get("v")?.asString ?: ""
                    val option1 = cells[18]?.asJsonObject?.get("v")?.asString ?: ""
                    val option2 = cells[19]?.asJsonObject?.get("v")?.asString ?: ""
                    val option3 = cells[20]?.asJsonObject?.get("v")?.asString ?: ""
                    val option4 = cells[21]?.asJsonObject?.get("v")?.asString ?: ""

                    // 🎯 PARSE CORRECT ANSWER (1-based from sheet, convert to 0-based for app)
                    val correctAnswerCell = cells[22]?.asJsonObject
                    val correctAnswerRaw = correctAnswerCell?.get("v")

                    val correctAnswerFromSheet = when {
                        correctAnswerRaw?.isJsonPrimitive == true -> {
                            val primitive = correctAnswerRaw.asJsonPrimitive
                            when {
                                primitive.isNumber -> primitive.asInt
                                primitive.isString -> primitive.asString.trim().toIntOrNull() ?: 1
                                else -> 1
                            }
                        }
                        else -> 1
                    }

                    // ✅ Convert from 1-based (Sheet) to 0-based (App)
                    // Sheet: 1,2,3,4 → App: 0,1,2,3
                    val correctAnswer = (correctAnswerFromSheet - 1).coerceIn(0, 3)

                    // 🔍 VALIDATION LOG
                    val options = listOf(option1, option2, option3, option4)
                    Log.d(TAG, """
                    ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                    Row $index: ${question.take(60)}...
                    Options:
                      1. $option1
                      2. $option2
                      3. $option3
                      4. $option4
                    Sheet Value: $correctAnswerFromSheet (1-based)
                    App Value: $correctAnswer (0-based)
                    ✅ Correct Answer: ${options.getOrNull(correctAnswer) ?: "INVALID"}
                    ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                """.trimIndent())

                    val explanation = cells[23]?.asJsonObject?.get("v")?.asString ?: ""
                    val marks = cells[24]?.asJsonObject?.get("v")?.asDouble ?: 1.0
                    val negativeMarks = cells[25]?.asJsonObject?.get("v")?.asDouble ?: 0.25

                    // Store category metadata
                    if (!categoryMetadata.containsKey(categoryId)) {
                        categoryMetadata[categoryId] = CategoryMetadata(
                            id = categoryId,
                            name = categoryName,
                            testsCount = testsCount,
                            quizzesCount = quizzesCount
                        )
                    }

                    // Store test metadata
                    if (!testMetadata.containsKey(testId)) {
                        testMetadata[testId] = TestMetadata(
                            id = testId,
                            title = title,
                            isFree = isFree,
                            isLiveTest = isLiveTest,
                            tags = tags,
                            questionsCount = questionsCount,
                            durationMinutes = durationMinutes,
                            totalMarks = totalMarks,
                            languages = languages,
                            endsInDays = endsInDays,
                            status = status,
                            examType = examType,
                            categoryId = categoryId
                        )
                    }

                    // Create quiz question
                    val quizQuestion = QuizQuestion(
                        id = questionId,
                        question = question,
                        options = options,
                        correctAnswer = correctAnswer,  // 0-based value
                        explanation = explanation,
                        marks = marks,
                        negativeMarks = negativeMarks
                    )

                    testsMap.getOrPut(testId) { mutableListOf() }.add(quizQuestion)

                } catch (e: Exception) {
                    Log.e(TAG, "❌ Error parsing row $index", e)
                }
            }

            // Build final structure
            val categoriesMap = mutableMapOf<String, MutableList<LiveTest>>()

            testMetadata.forEach { (testId, meta) ->
                val questions = testsMap[testId] ?: emptyList()
                val testWithQuestions = LiveTest(
                    id = meta.id,
                    title = meta.title,
                    isFree = meta.isFree,
                    isLiveTest = meta.isLiveTest,
                    tags = meta.tags,
                    questionsCount = meta.questionsCount,
                    durationMinutes = meta.durationMinutes,
                    totalMarks = meta.totalMarks,
                    languages = meta.languages,
                    endsInDays = meta.endsInDays,
                    status = meta.status,
                    examType = meta.examType,
                    categoryId = meta.categoryId,
                    questions = questions
                )

                categoriesMap.getOrPut(meta.categoryId) { mutableListOf() }.add(testWithQuestions)
            }

            val categories = categoriesMap.map { (categoryId, tests) ->
                val catMeta = categoryMetadata[categoryId]
                QuizCategory(
                    id = categoryId,
                    categoryName = catMeta?.name ?: categoryId,
                    testsCount = catMeta?.testsCount ?: tests.size,
                    quizzesCount = catMeta?.quizzesCount ?: tests.sumOf { it.questionsCount },
                    icon = "category",
                    tests = tests
                )
            }

            Log.d(TAG, "✅ Parsed ${categories.size} categories, ${testMetadata.size} tests")
            return QuizDataResponse(categories = categories)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error parsing Google Sheets data", e)
            return QuizDataResponse(categories = emptyList())
        }
    }


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
