package com.example.justbaatai

import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // This line loads your settings UI from the XML file
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        // --- Optional: Add click listeners for your settings ---

        // Find a preference by its key to make it clickable
        val logoutPreference: Preference? = findPreference("logout")
        logoutPreference?.setOnPreferenceClickListener {
            // TODO: Add your actual logout logic here
            Toast.makeText(requireContext(), "Logging out...", Toast.LENGTH_SHORT).show()
            true // True means the click was handled
        }

        val deleteAccountPreference: Preference? = findPreference("delete_account")
        deleteAccountPreference?.setOnPreferenceClickListener {
            // TODO: Show a confirmation dialog before deleting the account
            Toast.makeText(requireContext(), "Delete account clicked", Toast.LENGTH_SHORT).show()
            true
        }
    }
}