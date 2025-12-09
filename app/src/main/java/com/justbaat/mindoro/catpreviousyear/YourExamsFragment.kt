package com.justbaat.mindoro.catpreviousyear

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.justbaat.mindoro.R
import com.justbaat.mindoro.databinding.FragmentYourExamsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class YourExamsFragment : Fragment() {

    private var _binding: FragmentYourExamsBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: ExamCategoryAdapter
    private lateinit var examsAdapter: ExamsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentYourExamsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoriesRecyclerView()
        setupExamsRecyclerView()
//        setupBackButton()
    }

    private fun setupCategoriesRecyclerView() {
        val categories = listOf(
            ExamCategory("SSC Exams", 1, true),
            ExamCategory("Banking Exams", 1),
            ExamCategory("Teaching Exams", 1),
            ExamCategory("Railway Exams", 1),
            ExamCategory("Civil Services", 1)
        )

        categoryAdapter = ExamCategoryAdapter(categories) { category ->
            // Handle category click - filter exams based on selected category
            loadExamsForCategory(category)
        }

        binding.rvExamCategories.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
    }

    private fun setupExamsRecyclerView() {
        val examRepository = ExamRepository(requireContext())
        val sscExams = examRepository.getExamsForCategory("SSC Exams")

        // Convert to Exam list for adapter
        val exams = sscExams.map { examData ->
            Exam(
                name = examData.examName,
                papersCount = examData.totalPapers,
                iconRes = getIconForType(examData.iconType)
            )
        }

        examsAdapter = ExamsListAdapter(exams) { exam ->
            navigateToExamPapers(exam)
        }

        binding.rvExamsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = examsAdapter
        }
    }

    private fun getIconForType(iconType: String): Int {
        return when(iconType) {
            "ssc" -> R.drawable.ic_ssc_logo
            "police" -> R.drawable.ic_police_logo
            "ib" -> R.drawable.ic_ib_logo
            "banking" -> R.drawable.ic_exam_placeholder
            "teaching" -> R.drawable.ic_exam_placeholder
            else -> R.drawable.ic_exam_placeholder
        }
    }

    private fun getSSCExams(): List<Exam> {
        return listOf(
            Exam("SSC CGL", 3, R.drawable.ic_ssc_logo),
        )
    }

    private fun loadExamsForCategory(category: ExamCategory) {
        // TODO: Load exams based on selected category
        // For now, just showing SSC exams
        val exams = when (category.name) {
            "SSC Exams" -> getSSCExams()
            "Banking Exams" -> getBankingExams()
            "Teaching Exams" -> getTeachingExams()
            "Railway Exams" -> getRailwayExams()
            "Civil Services" -> getCivilServicesExams()
            else -> getSSCExams()
        }

        examsAdapter = ExamsListAdapter(exams) { exam ->
            navigateToExamPapers(exam)
        }
        binding.rvExamsList.adapter = examsAdapter
    }

    private fun getBankingExams(): List<Exam> {
        // TODO: Implement banking exams list
        return listOf(
            Exam("SEBI Grade A", 1, R.drawable.ic_police_logo),
        )
    }

    private fun getTeachingExams(): List<Exam> {
        // TODO: Implement teaching exams list
        return listOf(
            Exam("CTET", 1, R.drawable.ic_ssc_logo),
        )
    }

    private fun getCivilServicesExams(): List<Exam> {
        // TODO: Implement civil services exams list
        return listOf(
            Exam("UPSC EPFO", 1, R.drawable.ic_police_logo),
        )
    }

    private fun getRailwayExams(): List<Exam> {
        // TODO: Implement civil services exams list
        return listOf(
            Exam("RRB NTPC", 1, R.drawable.ic_police_logo),
        )
    }

    private fun navigateToExamPapers(exam: Exam) {
        ExamPapersActivity.start(requireContext(), exam.name)
    }



//    private fun setupBackButton() {
//        binding.btnBack.setOnClickListener {
//            requireActivity().onBackPressed()
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}