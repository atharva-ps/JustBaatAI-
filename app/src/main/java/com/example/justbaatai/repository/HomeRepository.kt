package com.example.justbaatai.repository

import com.example.justbaatai.HomeGridItem
import com.example.justbaatai.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor() {

    // This function provides the list of items for the home screen
    suspend fun getHomeGridItems(): List<HomeGridItem> {
        return withContext(Dispatchers.IO) {
            // In the future, you could fetch this data from a database or network
            listOf(
                HomeGridItem("Study Notes", "10,000+ Expert Notes", R.drawable.ic_grid_notes, R.color.purple_500, R.id.action_nav_home_to_nav_study_notes, "NEW"),
                HomeGridItem("Previous Year Papers", "Access 10,000+ PYPs", R.drawable.ic_grid_papers, R.color.yellow_700, R.id.action_nav_home_to_nav_your_exams),
                HomeGridItem("Quiz Section", "", R.drawable.ic_grid_quiz, R.color.purple_700, R.id.action_nav_home_to_nav_quizzes),
                HomeGridItem("Exam Books", "", R.drawable.ic_grid_books, R.color.red_500, R.id.action_nav_home_to_nav_books),
                HomeGridItem("Current Affairs", "", R.drawable.ic_grid_affairs, R.color.blue_700, R.id.action_nav_home_to_nav_daily_affairs)
            )
        }
    }
}