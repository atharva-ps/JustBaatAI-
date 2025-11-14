package com.justbaat.mindoro.cataboutus

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.justbaat.mindoro.databinding.FragmentAboutUsBinding

class AboutUsFragment : Fragment() {

    private var _binding: FragmentAboutUsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutUsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    private fun setupUI() {
        // Set app version - Get from PackageManager instead of BuildConfig
        try {
            val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            binding.tvVersion.text = "Version ${packageInfo.versionName}"
        } catch (e: Exception) {
            binding.tvVersion.text = "Version 1.0.0"
        }

        // Email click
        binding.btnContactEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("shivankur@justbaat.com")
                putExtra(Intent.EXTRA_SUBJECT, "Mindoro App - Feedback")
            }
            try {
                startActivity(Intent.createChooser(intent, "Send Email"))
            } catch (e: Exception) {
                // Handle if no email app is available
            }
        }


        // Privacy Policy click
        binding.btnPrivacyPolicy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/justbaat.com/mindoro/"))
            try {
                startActivity(intent)
            } catch (e: Exception) {
                // Handle if no browser is available
            }
        }


        // Share app
//        binding.btnShareApp.setOnClickListener {
//            val shareIntent = Intent().apply {
//                action = Intent.ACTION_SEND
//                type = "text/plain"
//                putExtra(Intent.EXTRA_SUBJECT, "Mindoro - Exam Preparation App")
//                putExtra(Intent.EXTRA_TEXT,
//                    "Check out Mindoro for SSC, Banking, Railway & other government exam preparation!\n\n" +
//                            "Download now: https://play.google.com/store/apps/details?id=${requireContext().packageName}")
//            }
//            try {
//                startActivity(Intent.createChooser(shareIntent, "Share Mindoro"))
//            } catch (e: Exception) {
//                // Handle if sharing fails
//            }
//        }

        // Rate app
//        binding.btnRateApp.setOnClickListener {
//            val uri = Uri.parse("market://details?id=${requireContext().packageName}")
//            val intent = Intent(Intent.ACTION_VIEW, uri)
//            try {
//                startActivity(intent)
//            } catch (e: Exception) {
//                // If Play Store is not available, open in browser
//                val webUri = Uri.parse("https://play.google.com/store/apps/details?id=${requireContext().packageName}")
//                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
//                try {
//                    startActivity(webIntent)
//                } catch (e2: Exception) {
//                    // Handle if browser is also not available
//                }
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
