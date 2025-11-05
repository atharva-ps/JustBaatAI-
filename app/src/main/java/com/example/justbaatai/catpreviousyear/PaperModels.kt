package com.example.justbaatai.catpreviousyear

import com.google.gson.annotations.SerializedName

data class ExamDataResponse(
    @SerializedName("categories")
    val categories: List<Category>
)

data class Category(
    @SerializedName("categoryName")
    val categoryName: String,
    @SerializedName("examCount")
    val examCount: Int,
    @SerializedName("exams")
    val exams: List<ExamData>
)

data class ExamData(
    @SerializedName("examName")
    val examName: String,
    @SerializedName("totalPapers")
    val totalPapers: Int,
    @SerializedName("iconType")
    val iconType: String,
    @SerializedName("yearGroups")
    val yearGroups: List<YearGroup>
)

data class YearGroup(
    @SerializedName("year")
    val year: String,
    @SerializedName("isExpanded")
    var isExpanded: Boolean = false,
    @SerializedName("papers")
    val papers: List<Paper>
)

data class Paper(
    @SerializedName("title")
    val title: String,
    @SerializedName("questionsCount")
    val questionsCount: Int,
    @SerializedName("durationMinutes")
    val durationMinutes: Int,
    @SerializedName("totalMarks")
    val totalMarks: Int,
    @SerializedName("languages")
    val languages: List<String>,
    @SerializedName("isPremium")
    val isPremium: Boolean = false,
    @SerializedName("pdfUrl")
    val pdfUrl: String = ""
)

