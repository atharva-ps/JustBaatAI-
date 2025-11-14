package com.justbaat.mindoro.catfreequizzes

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class QuizCategory(
    @SerializedName("id")
    val id: String,
    @SerializedName("categoryName")
    val categoryName: String,
    @SerializedName("testsCount")
    val testsCount: Int,
    @SerializedName("quizzesCount")
    val quizzesCount: Int,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("tests")
    val tests: List<LiveTest> = emptyList()
) : Serializable

data class LiveTest(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("isFree")
    val isFree: Boolean,
    @SerializedName("isLiveTest")
    val isLiveTest: Boolean,
    @SerializedName("tags")
    val tags: List<String>,
    @SerializedName("questionsCount")
    val questionsCount: Int,
    @SerializedName("durationMinutes")
    val durationMinutes: Int,
    @SerializedName("totalMarks")
    val totalMarks: Double,
    @SerializedName("languages")
    val languages: List<String>,
    @SerializedName("endsInDays")
    val endsInDays: Int,
    @SerializedName("status")
    val status: String, // "Start Now" or "Register"
    @SerializedName("examType")
    val examType: String,
    @SerializedName("categoryId")
    val categoryId: String,
    @SerializedName("questions")
    val questions: List<QuizQuestion> = emptyList()
) : Serializable



data class QuizQuestion(
    @SerializedName("id")
    val id: String,
    @SerializedName("question")
    val question: String,
    @SerializedName("options")
    val options: List<String>,
    @SerializedName("correctAnswer")
    val correctAnswer: Int, // Index of correct option (0-3)
    @SerializedName("explanation")
    val explanation: String? = null,
    @SerializedName("marks")
    val marks: Double = 1.0,
    @SerializedName("negativeMarks")
    val negativeMarks: Double = 0.25
) : Serializable

data class QuizSession(
    val testId: String,
    val testTitle: String,
    val questions: List<QuizQuestion>,
    val durationMinutes: Int,
    val totalMarks: Double,
    var currentQuestionIndex: Int = 0,
    val answers: MutableMap<Int, Int> = mutableMapOf(), // questionIndex -> selectedOptionIndex
    var startTime: Long = System.currentTimeMillis(),
    var isSubmitted: Boolean = false
) : Serializable

data class QuizResult(
    val testId: String,
    val testTitle: String,
    val totalQuestions: Int,
    val attempted: Int,
    val correct: Int,
    val incorrect: Int,
    val skipped: Int,
    val score: Double,
    val totalMarks: Double,
    val percentage: Double,
    val timeTaken: Long, // in milliseconds
    val timestamp: Long = System.currentTimeMillis()
) : Serializable
data class QuizDataResponse(
    @SerializedName("categories")
    val categories: List<QuizCategory>
)

// UI State for better state management
sealed class QuizUiState {
    object Loading : QuizUiState()
    data class Success(val categories: List<QuizCategory>, val tests: List<LiveTest>) : QuizUiState()
    data class Error(val message: String) : QuizUiState()
}
