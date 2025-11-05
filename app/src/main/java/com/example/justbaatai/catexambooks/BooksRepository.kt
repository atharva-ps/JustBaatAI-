package com.example.justbaatai.catexambooks

import android.content.Context
import com.example.justbaatai.R
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.InputStreamReader

data class BooksDataResponse(
    @SerializedName("categories")
    val categories: List<BookCategoryData>
)

data class BookCategoryData(
    @SerializedName("categoryName")
    val categoryName: String,
    @SerializedName("booksCount")
    val booksCount: Int,
    @SerializedName("books")
    val books: List<Book>
)

class BooksRepository(private val context: Context) {

    private var cachedData: BooksDataResponse? = null

    fun loadBooksData(): BooksDataResponse {
        if (cachedData != null) {
            return cachedData!!
        }

        val inputStream = context.resources.openRawResource(R.raw.books_data)
        val reader = InputStreamReader(inputStream)
        cachedData = Gson().fromJson(reader, BooksDataResponse::class.java)
        reader.close()

        return cachedData!!
    }

    fun getAllCategories(): List<BookCategory> {
        return loadBooksData().categories.map {
            BookCategory(it.categoryName, it.booksCount)
        }
    }

    fun getBooksForCategory(categoryName: String): List<Book> {
        return loadBooksData().categories
            .find { it.categoryName.equals(categoryName, ignoreCase = true) }
            ?.books ?: emptyList()
    }
}
