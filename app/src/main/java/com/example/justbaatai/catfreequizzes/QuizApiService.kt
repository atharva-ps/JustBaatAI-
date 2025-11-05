//package com.example.justbaatai.catfreequizzes
//
////import com.example.justbaatai.QuizDataResponse
//import retrofit2.Response
//import retrofit2.http.GET
//import retrofit2.http.Path
//import retrofit2.http.Query
//
//interface QuizApiService {
//
//    // Get all quiz categories with tests
//    @GET("api/v1/quizzes/categories")
//    suspend fun getQuizCategories(): Response<QuizDataResponse>
//
//    // Get specific category with tests
//    @GET("api/v1/quizzes/categories/{categoryId}")
//    suspend fun getCategoryById(
//        @Path("categoryId") categoryId: String
//    ): Response<QuizCategory>
//
//    // Get filtered tests
//    @GET("api/v1/quizzes/tests")
//    suspend fun getFilteredTests(
//        @Query("filterType") filterType: String,
//        @Query("categoryId") categoryId: String? = null
//    ): Response<List<LiveTest>>
//
//    // Search tests
//    @GET("api/v1/quizzes/search")
//    suspend fun searchTests(
//        @Query("q") query: String
//    ): Response<List<LiveTest>>
//
//    // Get test details
//    @GET("api/v1/quizzes/tests/{testId}")
//    suspend fun getTestById(
//        @Path("testId") testId: String
//    ): Response<LiveTest>
//}
