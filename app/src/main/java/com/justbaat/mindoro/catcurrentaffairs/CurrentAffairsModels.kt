package com.justbaat.mindoro.catcurrentaffairs


import com.google.gson.annotations.SerializedName

data class SuperCoachingCategory(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("coursesCount")
    val coursesCount: Int = 0,
    @SerializedName("courses")
    val courses: List<Course> = emptyList()
)

data class Course(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("features")
    val features: List<String>,
    @SerializedName("categoryId")
    val categoryId: String,
    @SerializedName("isBatch")
    val isBatch: Boolean = true,
    @SerializedName("url")
    val url: String = ""
)
