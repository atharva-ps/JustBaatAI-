package com.example.justbaatai.catfreetests


import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.justbaatai.databinding.ActivityTestBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding
    private lateinit var repository: MockTestRepository
    private var testId: String = ""
    private var currentTest: MockTest? = null
    private var currentQuestionIndex = 0
    private var userAnswers = mutableMapOf<Int, Int>()
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = MockTestRepository(this)
        testId = intent.getStringExtra("TEST_ID") ?: ""

        loadTest()
        setupUI()
    }

    private fun loadTest() {
        currentTest = repository.getTestById(testId)
        currentTest?.let { test ->
            // Use actual questions size, not questionsCount from JSON
            val actualQuestionCount = test.questions.size

            binding.tvTestTitle.text = test.title
            binding.tvQuestionCounter.text = "Question 1 of $actualQuestionCount"
            startTimer(test.durationMinutes * 60 * 1000L)

            if (test.questions.isNotEmpty()) {
                displayQuestion(0)
            } else {
                Log.e("TestActivity", "No questions found for test: ${test.title}")
                finish()
            }
        } ?: run {
            Log.e("TestActivity", "Test not found with ID: $testId")
            finish()
        }
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            showExitDialog()
        }

        binding.btnNext.setOnClickListener {
            saveAnswer()

            val actualQuestionCount = currentTest?.questions?.size ?: 0

            Log.d("TestActivity", "Current index: $currentQuestionIndex, Total: $actualQuestionCount")

            if (currentQuestionIndex < actualQuestionCount - 1) {
                // Move to next question
                currentQuestionIndex++
                displayQuestion(currentQuestionIndex)
            } else {
                // Last question - show submit confirmation
                showSubmitDialog()
            }
        }

        binding.btnPrevious.setOnClickListener {
            if (currentQuestionIndex > 0) {
                saveAnswer()
                currentQuestionIndex--
                displayQuestion(currentQuestionIndex)
            }
        }

        binding.btnSkip.setOnClickListener {
            // Skip without saving answer
            val actualQuestionCount = currentTest?.questions?.size ?: 0

            if (currentQuestionIndex < actualQuestionCount - 1) {
                currentQuestionIndex++
                displayQuestion(currentQuestionIndex)
            }
        }
    }

    private fun displayQuestion(index: Int) {
        currentTest?.questions?.getOrNull(index)?.let { question ->
            val actualQuestionCount = currentTest?.questions?.size ?: 0

            binding.tvQuestionCounter.text = "Question ${index + 1} of $actualQuestionCount"
            binding.tvQuestion.text = question.questionText

            // Set options
            if (question.options.size >= 4) {
                binding.rbOption1.text = question.options[0]
                binding.rbOption2.text = question.options[1]
                binding.rbOption3.text = question.options[2]
                binding.rbOption4.text = question.options[3]
            } else {
                Log.e("TestActivity", "Not enough options for question: ${question.questionText}")
            }

            // Clear previous selection first
            binding.radioGroup.clearCheck()

            // Small delay to ensure UI updates
            binding.radioGroup.postDelayed({
                // Restore previous answer if exists
                userAnswers[index]?.let { answer ->
                    when (answer) {
                        0 -> binding.rbOption1.isChecked = true
                        1 -> binding.rbOption2.isChecked = true
                        2 -> binding.rbOption3.isChecked = true
                        3 -> binding.rbOption4.isChecked = true
                    }
                }
            }, 50)

            // Update button states
            binding.btnPrevious.isEnabled = index > 0
            binding.btnNext.text = if (index == actualQuestionCount - 1) "Submit Test" else "Next Question"

            Log.d("TestActivity", "Displaying question ${index + 1} of $actualQuestionCount")
        }
    }

    private fun saveAnswer() {
        val selectedId = binding.radioGroup.checkedRadioButtonId
        if (selectedId != -1) {
            val selectedIndex = when (selectedId) {
                binding.rbOption1.id -> 0
                binding.rbOption2.id -> 1
                binding.rbOption3.id -> 2
                binding.rbOption4.id -> 3
                else -> -1
            }

            if (selectedIndex != -1) {
                userAnswers[currentQuestionIndex] = selectedIndex
                Log.d("TestActivity", "Saved answer for Q${currentQuestionIndex + 1}: Option ${selectedIndex + 1}")
            }
        }
    }

    private fun startTimer(milliseconds: Long) {
        timer = object : CountDownTimer(milliseconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)

                // Change timer color when less than 2 minutes
                if (millisUntilFinished < 120000) {
                    binding.tvTimer.setTextColor(getColor(android.R.color.holo_red_dark))
                }
            }

            override fun onFinish() {
                runOnUiThread {
                    submitTest()
                }
            }
        }.start()
    }

    private fun showSubmitDialog() {
        val actualQuestionCount = currentTest?.questions?.size ?: 0
        val answered = userAnswers.size
        val unanswered = actualQuestionCount - answered

        AlertDialog.Builder(this)
            .setTitle("Submit Test?")
            .setMessage("You have answered $answered out of $actualQuestionCount questions.\n$unanswered questions are unanswered.\n\nAre you sure you want to submit?")
            .setPositiveButton("Submit") { _, _ ->
                submitTest()
            }
            .setNegativeButton("Review") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setTitle("Exit Test?")
            .setMessage("Your progress will be lost. Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                timer?.cancel()
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun submitTest() {
        timer?.cancel()

        // Calculate results
        var correctAnswers = 0
        currentTest?.questions?.forEachIndexed { index, question ->
            userAnswers[index]?.let { userAnswer ->
                if (userAnswer == question.correctAnswer) {
                    correctAnswers++
                }
            }
        }

        val actualQuestionCount = currentTest?.questions?.size ?: 0
        val wrongAnswers = userAnswers.size - correctAnswers
        val skippedAnswers = actualQuestionCount - userAnswers.size

        // Show results
        showResultsDialog(correctAnswers, wrongAnswers, skippedAnswers, actualQuestionCount)
    }

    private fun showResultsDialog(correct: Int, wrong: Int, skipped: Int, total: Int) {
        val percentage = if (total > 0) (correct * 100) / total else 0

        AlertDialog.Builder(this)
            .setTitle("Test Completed! 🎉")
            .setMessage(
                "Score: $percentage%\n\n" +
                        "✅ Correct: $correct\n" +
                        "❌ Wrong: $wrong\n" +
                        "⏭️ Skipped: $skipped\n" +
                        "📊 Total: $total"
            )
            .setPositiveButton("OK") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onBackPressed() {
        showExitDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
