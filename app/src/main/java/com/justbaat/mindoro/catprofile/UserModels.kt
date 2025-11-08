package com.justbaat.mindoro.catprofile

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserProfile(
    @SerializedName("userId")
    val userId: String = "",
    @SerializedName("userName")
    val userName: String = "Guest User",
    @SerializedName("userEmail")
    val userEmail: String = "",
    @SerializedName("userMobile")
    val userMobile: String = "",
    @SerializedName("dateOfBirth")
    val dateOfBirth: String = "",
    @SerializedName("category")
    val category: String = "",
    @SerializedName("education")
    val education: String = "",
    @SerializedName("profilePicture")
    val profilePicture: String? = null,
    @SerializedName("isGuest")
    val isGuest: Boolean = true,
    @SerializedName("createdAt")
    val createdAt: Long = System.currentTimeMillis()
) : Serializable

enum class ExamCategory(val displayName: String) {
    SSC("SSC Exams"),
    UPSC("UPSC Exams"),
    BANKING("Banking Exams"),
    RAILWAY("Railway Exams"),
    TEACHING("Teaching Exams"),
    CIVIL("Civil Services"),
    OTHER("Other")
}

enum class EducationLevel(val displayName: String) {
    HIGHSCHOOL("High School"),
    INTERMEDIATE("Intermediate/12th"),
    GRADUATION("Graduation"),
    POSTGRADUATE("Post Graduation"),
    OTHER("Other")
}
