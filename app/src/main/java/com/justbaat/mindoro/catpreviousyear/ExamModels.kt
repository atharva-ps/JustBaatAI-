package com.justbaat.mindoro.catpreviousyear

data class ExamCategory(
    val name: String,
    val examCount: Int,
    val isSelected: Boolean = false
)

data class Exam(
    val name: String,
    val papersCount: Int,
    val iconRes: Int
)
