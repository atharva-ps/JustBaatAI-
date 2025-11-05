package com.example.justbaatai.catpreviousyear

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justbaatai.databinding.ActivityExamPapersBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExamPapersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExamPapersBinding
    private lateinit var papersAdapter: PapersAdapter
    private lateinit var examRepository: ExamRepository
    private var examName: String = ""

    companion object {
        private const val EXTRA_EXAM_NAME = "exam_name"
        private const val STORAGE_PERMISSION_CODE = 100

        fun start(context: Context, examName: String) {
            val intent = Intent(context, ExamPapersActivity::class.java).apply {
                putExtra(EXTRA_EXAM_NAME, examName)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivityExamPapersBinding.inflate(layoutInflater)
            setContentView(binding.root)

            examName = intent.getStringExtra(EXTRA_EXAM_NAME) ?: "SSC CGL"
            examRepository = ExamRepository(this)

            setupToolbar()
            setupRecyclerView()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupToolbar() {
        binding.tvExamTitle.text = "PYP - $examName"
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        // Load papers from repository (JSON or backend)
        val yearGroups = examRepository.getPapersForExam(examName).toMutableList()

        papersAdapter = PapersAdapter(
            yearGroups,
            onDownloadClick = { paper ->
                handleDownloadPdf(paper)
            },
            onUnlockClick = { paper ->
                handleUnlockTest(paper)
            }
        )

        binding.rvPapers.apply {
            layoutManager = LinearLayoutManager(this@ExamPapersActivity)
            adapter = papersAdapter
        }
    }

    private fun handleDownloadPdf(paper: Paper) {
        if (paper.pdfUrl.isEmpty()) {
            Toast.makeText(this, "PDF not available", Toast.LENGTH_SHORT).show()
            return
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_CODE
                )
                return
            }
        }

        try {
            val fileName = "${paper.title.replace("[^a-zA-Z0-9\\s]".toRegex(), "_")}.pdf"

            val request = DownloadManager.Request(Uri.parse(paper.pdfUrl))
                .setTitle(paper.title)
                .setDescription("Downloading ${paper.title}")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setMimeType("application/pdf")

            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadId = downloadManager.enqueue(request)

            Toast.makeText(this, "Download started...", Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted! Please try downloading again.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission denied! Cannot download files.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleUnlockTest(paper: Paper) {
        if (paper.isPremium) {
            Toast.makeText(this, "Premium feature - Subscribe to unlock", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Starting test: ${paper.title}", Toast.LENGTH_SHORT).show()
        }
    }
}
