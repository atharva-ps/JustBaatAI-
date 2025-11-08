package com.justbaat.mindoro

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.justbaat.mindoro.R
import com.justbaat.mindoro.databinding.FragmentSupportBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupportFragment : Fragment() {

    // The modern, safe way to handle View Binding in Fragments
    private var _binding: FragmentSupportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set click listener for the FAQ card
        binding.faqCard.setOnClickListener {
            // TODO: Navigate to a new Fragment or Activity that shows the FAQ list
            Toast.makeText(requireContext(), "Opening FAQs...", Toast.LENGTH_SHORT).show()
        }

        // Set click listener for the Contact Us card
        binding.contactCard.setOnClickListener {
            openEmailClient()
        }
    }

    private fun openEmailClient() {
        // Create an Intent to send an email
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            // Use mailto: to specify the email address
            data = Uri.parse("mailto:")
            // Add the recipient's email address
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            // Add a subject line
            putExtra(Intent.EXTRA_SUBJECT, "Support Request for JustBaat AI App")
        }

        // Check if there is an app that can handle this intent
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            // Show an error message if no email app is installed
            Toast.makeText(requireContext(), "No email app found.", Toast.LENGTH_SHORT).show()
        }
    }

    // This is crucial to prevent memory leaks in Fragments
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}