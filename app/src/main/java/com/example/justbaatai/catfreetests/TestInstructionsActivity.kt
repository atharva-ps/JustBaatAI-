package com.example.justbaatai.catfreetests


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.justbaatai.databinding.ActivityTestInstructionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestInstructionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestInstructionsBinding
    private lateinit var repository: MockTestRepository
    private var testId: String = ""
    private var currentTest: MockTest? = null

    companion object {
        private const val EXTRA_TEST_ID = "test_id"

        fun start(context: Context, testId: String) {
            val intent = Intent(context, TestInstructionsActivity::class.java).apply {
                putExtra(EXTRA_TEST_ID, testId)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestInstructionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = MockTestRepository(this)
        testId = intent.getStringExtra(EXTRA_TEST_ID) ?: ""

        loadTestInfo()
        setupUI()
    }

    private fun loadTestInfo() {
        currentTest = repository.getTestById(testId)
        currentTest?.let { test ->
            binding.tvTestTitle.text = test.title
            binding.tvTestDescription.text = test.description
            binding.tvQuestionsCount.text = "${test.questionsCount} Questions"
            binding.tvDuration.text = "${test.durationMinutes} Minutes"
            binding.tvDifficulty.text = test.difficulty
            binding.tvTopics.text = test.topics.joinToString(", ")
        }
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnStartTest.setOnClickListener {
            val intent = Intent(this, TestActivity::class.java).apply {
                putExtra("TEST_ID", testId)
            }
            startActivity(intent)
            finish()
        }
    }
}
