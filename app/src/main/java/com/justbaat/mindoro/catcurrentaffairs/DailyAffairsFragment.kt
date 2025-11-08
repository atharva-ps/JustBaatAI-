package com.justbaat.mindoro.catcurrentaffairs


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.justbaat.mindoro.catcurrentaffairs.CurrentAffairsRepository
import com.justbaat.mindoro.catcurrentaffairs.SuperCoachingCategoriesAdapter
import com.justbaat.mindoro.catcurrentaffairs.SuperCoachingCategory
import com.justbaat.mindoro.databinding.FragmentDailyAffairsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DailyAffairsFragment : Fragment() {

    private var _binding: FragmentDailyAffairsBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: CurrentAffairsRepository
    private lateinit var categoriesAdapter: SuperCoachingCategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyAffairsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = CurrentAffairsRepository(requireContext())

//        setupBackButton()
        setupSuperCoachingCategories()
    }

//    private fun setupBackButton() {
//        binding.btnBack.setOnClickListener {
//            requireActivity().onBackPressed()
//        }
//    }

    private fun setupSuperCoachingCategories() {
        val categories = repository.getAllSuperCoachingCategories()

        categoriesAdapter = SuperCoachingCategoriesAdapter(categories) { category ->
            handleCategoryClick(category)
        }

        binding.rvSuperCoachingCategories.apply {
            layoutManager = GridLayoutManager(context, 2) // 2 columns grid
            adapter = categoriesAdapter
        }
    }

    private fun handleCategoryClick(category: SuperCoachingCategory) {
        CategoryCoursesActivity.start(
            requireContext(),
            category.id,
            category.title.replace("\n", " ")
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
