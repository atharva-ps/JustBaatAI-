package com.justbaat.mindoro.catexambooks

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.justbaat.mindoro.databinding.FragmentBooksBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BooksFragment : Fragment() {

    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!

    private lateinit var booksRepository: BooksRepository
    private lateinit var categoryAdapter: BookCategoryAdapter
    private lateinit var booksAdapter: BooksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        booksRepository = BooksRepository(requireContext())

//        setupBackButton()
        setupCategoriesRecyclerView()
        setupBooksRecyclerView("Recommended")
    }

//    private fun setupBackButton() {
//        binding.btnBack.setOnClickListener {
//            requireActivity().onBackPressed()
//        }
//    }

    private fun setupCategoriesRecyclerView() {
        val categories = booksRepository.getAllCategories()

        categoryAdapter = BookCategoryAdapter(categories) { category ->
            setupBooksRecyclerView(category.categoryName)
        }

        binding.rvBookCategories.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
    }

    private fun setupBooksRecyclerView(categoryName: String) {
        val books = booksRepository.getBooksForCategory(categoryName)

        booksAdapter = BooksAdapter(books) { book ->
            handleBookClick(book)
        }

        binding.rvBooks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = booksAdapter
        }
    }

    private fun handleBookClick(book: Book) {
        if (book.amazonUrl.isNotEmpty()) {
            // Open Amazon link
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(book.amazonUrl))
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Recommended: ${book.title}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
