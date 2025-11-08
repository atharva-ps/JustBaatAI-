package com.justbaat.mindoro // Corrected package name

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.justbaat.mindoro.databinding.FragmentReferEarnBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReferEarnFragment : Fragment() {

    private var _binding: FragmentReferEarnBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReferEarnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.shareButton.setOnClickListener {
            shareReferralCode()
        }

        // Add a click listener for the new copy button
        binding.copyButton.setOnClickListener {
            copyCodeToClipboard()
        }
    }

    private fun shareReferralCode() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        val referralCode = binding.referralCodeText.text.toString()
        val shareMessage = "Hey! Check out this awesome app. Use my referral code to get a bonus: $referralCode"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "Share your code via..."))
    }

    private fun copyCodeToClipboard() {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val code = binding.referralCodeText.text
        val clip = ClipData.newPlainText("referral_code", code)
        clipboard.setPrimaryClip(clip)

        // Show a confirmation message
        Toast.makeText(requireContext(), R.string.referral_code_copied, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}