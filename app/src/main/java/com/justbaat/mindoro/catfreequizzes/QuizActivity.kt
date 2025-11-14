package com.justbaat.mindoro.catfreequizzes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.justbaat.mindoro.R
import com.justbaat.mindoro.databinding.QuizActivityBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class QuizActivity : AppCompatActivity() {

    private lateinit var binding: QuizActivityBinding
    private lateinit var quizSession: QuizSession
    private var countDownTimer: CountDownTimer? = null
    private val markedQuestions = mutableSetOf<Int>()

    companion object {
        private const val EXTRA_TEST = "extra_test"
        private const val EXTRA_QUESTIONS = "extra_questions"

        fun start(context: Context, test: LiveTest, questions: List<QuizQuestion>) {
            val intent = Intent(context, QuizActivity::class.java).apply {
                putExtra(EXTRA_TEST, test)
                putExtra(EXTRA_QUESTIONS, ArrayList(questions))
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = QuizActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val test = intent.getSerializableExtra(EXTRA_TEST) as? LiveTest
        @Suppress("UNCHECKED_CAST")
        val questions = intent.getSerializableExtra(EXTRA_QUESTIONS) as? ArrayList<QuizQuestion>

        if (test == null || questions.isNullOrEmpty()) {
            finish()
            return
        }

        initializeQuizSession(test, questions)
        setupUI()
        startTimer()
        loadQuestion()
    }

    private fun initializeQuizSession(test: LiveTest, questions: List<QuizQuestion>) {
        quizSession = QuizSession(
            testId = test.id,
            testTitle = test.title,
            questions = questions,
            durationMinutes = test.durationMinutes,
            totalMarks = test.totalMarks
        )
    }

    private fun setupUI() {
        binding.tvQuizTitle.text = quizSession.testTitle

        // Back button
        binding.btnBack.setOnClickListener {
            showExitConfirmation()
        }

        // Mark for review
        binding.btnMarkForReview.setOnClickListener {
            markForReview()
        }

        // Navigation buttons
        binding.btnPrevious.setOnClickListener {
            saveCurrentAnswer()
            navigateToPrevious()
        }

        binding.btnNext.setOnClickListener {
            saveCurrentAnswer()
            navigateToNext()
        }

        binding.btnSubmit.setOnClickListener {
            showSubmitConfirmation()
        }

        // Save answer on option selection
        binding.rgOptions.setOnCheckedChangeListener { _, _ ->
            saveCurrentAnswer()
        }
    }

    private fun loadQuestion() {
        val currentIndex = quizSession.currentQuestionIndex
        val question = quizSession.questions[currentIndex]
        val totalQuestions = quizSession.questions.size

        // Update UI
        binding.tvQuestionNumber.text = "Question ${currentIndex + 1}/$totalQuestions"
        binding.tvQuestionText.text = question.question
        binding.tvMarks.text = "+${question.marks} | -${question.negativeMarks}"

        // Load options
        binding.rbOption1.text = question.options.getOrNull(0) ?: ""
        binding.rbOption2.text = question.options.getOrNull(1) ?: ""
        binding.rbOption3.text = question.options.getOrNull(2) ?: ""
        binding.rbOption4.text = question.options.getOrNull(3) ?: ""

        // Clear selection
        binding.rgOptions.clearCheck()

        // Restore previous answer if exists
        quizSession.answers[currentIndex]?.let { selectedOption ->
            when (selectedOption) {
                0 -> binding.rbOption1.isChecked = true
                1 -> binding.rbOption2.isChecked = true
                2 -> binding.rbOption3.isChecked = true
                3 -> binding.rbOption4.isChecked = true
            }
        }

        // Update button states
        binding.btnPrevious.isEnabled = currentIndex > 0

        if (currentIndex == totalQuestions - 1) {
            binding.btnNext.visibility = View.GONE
            binding.btnSubmit.visibility = View.VISIBLE
        } else {
            binding.btnNext.visibility = View.VISIBLE
            binding.btnSubmit.visibility = View.GONE
        }

        // Update status
        updateQuestionStatus()

        // Update mark for review button state
        if (markedQuestions.contains(currentIndex)) {
            binding.btnMarkForReview.text = "Unmark Review"
        } else {
            binding.btnMarkForReview.text = "Mark for Review"
        }
    }

    private fun saveCurrentAnswer() {
        val currentIndex = quizSession.currentQuestionIndex
        val selectedOption = when (binding.rgOptions.checkedRadioButtonId) {
            binding.rbOption1.id -> 0
            binding.rbOption2.id -> 1
            binding.rbOption3.id -> 2
            binding.rbOption4.id -> 3
            else -> -1
        }

        if (selectedOption != -1) {
            quizSession.answers[currentIndex] = selectedOption
        } else {
            quizSession.answers.remove(currentIndex)
        }

        updateQuestionStatus()
    }

    private fun updateQuestionStatus() {
        val answered = quizSession.answers.size
        val marked = markedQuestions.size
        val notVisited = quizSession.questions.size - answered - marked

        binding.tvQuestionStatus.text = "Answered: $answered/${quizSession.questions.size}\n" +
                "Marked: $marked\n" +
                "Not Visited: ${notVisited.coerceAtLeast(0)}"
    }

    private fun markForReview() {
        val currentIndex = quizSession.currentQuestionIndex
        if (markedQuestions.contains(currentIndex)) {
            markedQuestions.remove(currentIndex)
            binding.btnMarkForReview.text = "Mark for Review"
        } else {
            markedQuestions.add(currentIndex)
            binding.btnMarkForReview.text = "Unmark Review"
        }
        updateQuestionStatus()
    }

    private fun navigateToPrevious() {
        if (quizSession.currentQuestionIndex > 0) {
            quizSession.currentQuestionIndex--
            loadQuestion()
        }
    }

    private fun navigateToNext() {
        if (quizSession.currentQuestionIndex < quizSession.questions.size - 1) {
            quizSession.currentQuestionIndex++
            loadQuestion()
        }
    }

    private fun startTimer() {
        val totalMillis = TimeUnit.MINUTES.toMillis(quizSession.durationMinutes.toLong())

        countDownTimer = object : CountDownTimer(totalMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60

                binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)

                // Change color when less than 5 minutes
                if (millisUntilFinished < TimeUnit.MINUTES.toMillis(5)) {
                    binding.tvTimer.setTextColor(
                        ContextCompat.getColor(this@QuizActivity, R.color.red_500)
                    )
                }
            }

            override fun onFinish() {
                binding.tvTimer.text = "00:00"
                submitQuiz()
            }
        }.start()
    }

    private fun showExitConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Exit Quiz")
            .setMessage("Are you sure you want to exit? Your progress will be lost.")
            .setPositiveButton("Exit") { _, _ ->
                countDownTimer?.cancel()
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSubmitConfirmation() {
        saveCurrentAnswer()

        val attempted = quizSession.answers.size
        val skipped = quizSession.questions.size - attempted

        AlertDialog.Builder(this)
            .setTitle("Submit Quiz")
            .setMessage("Attempted: $attempted\nSkipped: $skipped\nMarked: ${markedQuestions.size}\n\nAre you sure you want to submit?")
            .setPositiveButton("Submit") { _, _ ->
                submitQuiz()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun submitQuiz() {
        countDownTimer?.cancel()
        quizSession.isSubmitted = true

        val result = calculateResult()

        // Show result dialog
        showResultDialog(result)
    }

    private fun showResultDialog(result: QuizResult) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_quiz_result, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Make dialog background transparent for rounded corners
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Get views
        val ivResultIcon = dialogView.findViewById<ImageView>(R.id.ivResultIcon)
        val tvResultTitle = dialogView.findViewById<TextView>(R.id.tvResultTitle)
        val tvScore = dialogView.findViewById<TextView>(R.id.tvScore)
        val tvPercentage = dialogView.findViewById<TextView>(R.id.tvPercentage)
        val tvTotalQuestions = dialogView.findViewById<TextView>(R.id.tvTotalQuestions)
        val tvAttempted = dialogView.findViewById<TextView>(R.id.tvAttempted)
        val tvCorrect = dialogView.findViewById<TextView>(R.id.tvCorrect)
        val tvIncorrect = dialogView.findViewById<TextView>(R.id.tvIncorrect)
        val tvSkipped = dialogView.findViewById<TextView>(R.id.tvSkipped)
        val tvTimeTaken = dialogView.findViewById<TextView>(R.id.tvTimeTaken)
        val btnViewSolutions = dialogView.findViewById<Button>(R.id.btnViewSolutions)
        val btnRetakeQuiz = dialogView.findViewById<Button>(R.id.btnRetakeQuiz)
        val btnClose = dialogView.findViewById<Button>(R.id.btnClose)

        // Set data
        tvScore.text = String.format("%.2f / %.2f", result.score, result.totalMarks)
        tvPercentage.text = String.format("%.2f%%", result.percentage)
        tvTotalQuestions.text = result.totalQuestions.toString()
        tvAttempted.text = result.attempted.toString()
        tvCorrect.text = result.correct.toString()
        tvIncorrect.text = result.incorrect.toString()
        tvSkipped.text = result.skipped.toString()

        // Format time taken
        val minutes = TimeUnit.MILLISECONDS.toMinutes(result.timeTaken)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(result.timeTaken) % 60
        tvTimeTaken.text = String.format("%02d:%02d", minutes, seconds)

        // Set result message and icon based on percentage
        val (message, iconRes, scoreColor) = when {
            result.percentage >= 80 -> Triple(
                "Excellent! 🎉",
                R.drawable.ic_emoji_point_up,
                ContextCompat.getColor(this, R.color.color_study_green)
            )
            result.percentage >= 60 -> Triple(
                "Good Job! 👍",
                R.drawable.ic_emoji_point_up,
                ContextCompat.getColor(this, R.color.color_study_green)
            )
            result.percentage >= 40 -> Triple(
                "Keep Practicing 💪",
                R.drawable.ic_warning,
                ContextCompat.getColor(this, R.color.yellow_700)
            )
            else -> Triple(
                "Need More Practice 📚",
                R.drawable.ic_error,
                ContextCompat.getColor(this, R.color.red_500)
            )
        }

        tvResultTitle.text = message
        ivResultIcon.setImageResource(iconRes)
        ivResultIcon.setColorFilter(scoreColor)
        tvScore.setTextColor(scoreColor)

        // Button actions
        btnViewSolutions.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Solutions - Coming Soon!", Toast.LENGTH_SHORT).show()
        }

        btnRetakeQuiz.setOnClickListener {
            dialog.dismiss()
            recreate()
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    private fun calculateResult(): QuizResult {
        var correct = 0
        var incorrect = 0
        var score = 0.0

        quizSession.answers.forEach { (questionIndex, selectedOption) ->
            val question = quizSession.questions[questionIndex]
            if (selectedOption == question.correctAnswer) {
                correct++
                score += question.marks
            } else {
                incorrect++
                score -= question.negativeMarks
            }
        }

        val attempted = quizSession.answers.size
        val skipped = quizSession.questions.size - attempted
        val timeTaken = System.currentTimeMillis() - quizSession.startTime
        val percentage = ((score / quizSession.totalMarks) * 100).coerceAtLeast(0.0)

        return QuizResult(
            testId = quizSession.testId,
            testTitle = quizSession.testTitle,
            totalQuestions = quizSession.questions.size,
            attempted = attempted,
            correct = correct,
            incorrect = incorrect,
            skipped = skipped,
            score = score.coerceAtLeast(0.0),
            totalMarks = quizSession.totalMarks,
            percentage = percentage,
            timeTaken = timeTaken
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
