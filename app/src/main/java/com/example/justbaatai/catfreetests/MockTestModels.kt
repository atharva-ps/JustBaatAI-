package com.example.justbaatai.catfreetests


import com.google.gson.annotations.SerializedName

data class MockTestCategory(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("testsCount")
    val testsCount: Int,
    @SerializedName("tests")
    val tests: List<MockTest>
)

data class MockTest(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("questionsCount")
    val questionsCount: Int,
    @SerializedName("durationMinutes")
    val durationMinutes: Int,
    @SerializedName("categoryId")
    val categoryId: String,
    @SerializedName("difficulty")
    val difficulty: String, // "Easy", "Medium", "Hard"
    @SerializedName("topics")
    val topics: List<String>,
    @SerializedName("questions")
    val questions: List<Question> = emptyList()
)

data class Question(
    @SerializedName("id")
    val id: String,
    @SerializedName("questionText")
    val questionText: String,
    @SerializedName("options")
    val options: List<String>,
    @SerializedName("correctAnswer")
    val correctAnswer: Int, // Index of correct option (0-3)
    @SerializedName("explanation")
    val explanation: String = "",
    @SerializedName("topic")
    val topic: String = ""
)

data class TestResult(
    @SerializedName("testId")
    val testId: String,
    @SerializedName("userId")
    val userId: String = "",
    @SerializedName("score")
    val score: Int,
    @SerializedName("totalQuestions")
    val totalQuestions: Int,
    @SerializedName("timeTaken")
    val timeTaken: Int, // in seconds
    @SerializedName("correctAnswers")
    val correctAnswers: Int,
    @SerializedName("wrongAnswers")
    val wrongAnswers: Int,
    @SerializedName("skippedAnswers")
    val skippedAnswers: Int,
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
)
