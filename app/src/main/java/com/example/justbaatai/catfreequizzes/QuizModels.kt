package com.example.justbaatai.catfreequizzes

import com.google.gson.annotations.SerializedName

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
)

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
    val categoryId: String
)
