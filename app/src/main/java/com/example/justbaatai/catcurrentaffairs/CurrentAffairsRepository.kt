package com.example.justbaatai.catcurrentaffairs


import android.content.Context
import com.example.justbaatai.R
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.InputStreamReader

data class CurrentAffairsDataResponse(
    @SerializedName("superCoachingCategories")
    val superCoachingCategories: List<SuperCoachingCategory>
)

class CurrentAffairsRepository(private val context: Context) {

    private var cachedData: CurrentAffairsDataResponse? = null

    fun loadCurrentAffairsData(): CurrentAffairsDataResponse {
        if (cachedData != null) {
            return cachedData!!
        }

        val inputStream = context.resources.openRawResource(R.raw.current_affairs_data)
        val reader = InputStreamReader(inputStream)
        cachedData = Gson().fromJson(reader, CurrentAffairsDataResponse::class.java)
        reader.close()

        return cachedData!!
    }

    fun getAllSuperCoachingCategories(): List<SuperCoachingCategory> {
        return loadCurrentAffairsData().superCoachingCategories
    }
    fun getCategoryWithCourses(categoryId: String): SuperCoachingCategory? {
        return loadCurrentAffairsData().superCoachingCategories
            .find { it.id == categoryId }
    }


    fun getCategoryById(categoryId: String): SuperCoachingCategory? {
        return loadCurrentAffairsData().superCoachingCategories
            .find { it.id == categoryId }
    }
}
