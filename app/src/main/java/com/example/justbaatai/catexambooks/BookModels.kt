package com.example.justbaatai.catexambooks

import com.google.gson.annotations.SerializedName

data class BookCategory(
    @SerializedName("categoryName")
    val categoryName: String,
    @SerializedName("booksCount")
    val booksCount: Int
)

data class Book(
    @SerializedName("title")
    val title: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("subjects")
    val subjects: List<String>,
    @SerializedName("language")
    val language: String,
    @SerializedName("description")
    val description: String = "",
    @SerializedName("examType")
    val examType: String = "",
    @SerializedName("amazonUrl")
    val amazonUrl: String = ""
)
