package com.justbaat.mindoro.catprofile

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.justbaat.mindoro.databinding.DialogSelectCategoryBinding
import com.justbaat.mindoro.databinding.DialogSelectEducationBinding
import com.justbaat.mindoro.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.util.Calendar

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ProfileFragment", "onViewCreated called")

        observeViewModelData()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // DOB field
        binding.dobEditText.isFocusable = false
        binding.dobEditText.setOnClickListener {
            Log.d("ProfileFragment", "DOB clicked")
            showDatePickerDialog()
        }

        // Category field
        binding.categoryEditText.isFocusable = false
        binding.categoryEditText.setOnClickListener {
            Log.d("ProfileFragment", "Category clicked")
            showCategoryDialog()
        }

        // Education field
        binding.educationEditText.isFocusable = false
        binding.educationEditText.setOnClickListener {
            Log.d("ProfileFragment", "Education clicked")
            showEducationDialog()
        }

        // Edit photo button
        binding.editPhotoButton.setOnClickListener {
            Toast.makeText(requireContext(), "Photo upload coming soon", Toast.LENGTH_SHORT).show()
        }

        // Save profile button
        binding.saveProfileButton.setOnClickListener {
            Log.d("ProfileFragment", "Save button clicked")
            saveDataToViewModel()
        }
    }

    private fun observeViewModelData() {
        sharedViewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            Log.d("ProfileFragment", "Profile updated: $profile")

            // Populate all fields
            binding.nameEditText.setText(profile.userName)
            binding.emailEditText.setText(profile.userEmail)
            binding.mobileEditText.setText(profile.userMobile)
            binding.dobEditText.setText(profile.dateOfBirth)
            binding.categoryEditText.setText(profile.category)
            binding.educationEditText.setText(profile.education)

            // Set profile initials
            val initials = sharedViewModel.getInitials(profile.userName)
            binding.profileInitials.text = initials

            // Update guest badge if needed
            if (profile.isGuest) {
                binding.guestBadge.visibility = View.VISIBLE
            }
        }

        sharedViewModel.isSaving.observe(viewLifecycleOwner) { isSaving ->
            Log.d("ProfileFragment", "Saving state: $isSaving")
            binding.saveProfileButton.isEnabled = !isSaving
            if (isSaving) {
                binding.saveProfileButton.text = "Saving..."
            } else {
                binding.saveProfileButton.text = "Save Profile"
            }
        }
    }

    private fun saveDataToViewModel() {
        // Get current values from UI
        val name = binding.nameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val mobile = binding.mobileEditText.text.toString().trim()
        val dob = binding.dobEditText.text.toString().trim()
        val category = binding.categoryEditText.text.toString().trim()
        val education = binding.educationEditText.text.toString().trim()

        // Validation
        if (name.isEmpty()) {
            binding.nameEditText.error = "Name is required"
            Toast.makeText(requireContext(), "Please enter your name", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.isEmpty()) {
            binding.emailEditText.error = "Email is required"
            Toast.makeText(requireContext(), "Please enter your email", Toast.LENGTH_SHORT).show()
            return
        }

        if (mobile.isEmpty()) {
            binding.mobileEditText.error = "Mobile is required"
            Toast.makeText(requireContext(), "Please enter your mobile number", Toast.LENGTH_SHORT).show()
            return
        }

        // Get current profile or create new one
        val currentProfile = sharedViewModel.userProfile.value ?: UserProfile()

        Log.d("ProfileFragment", "Current profile: $currentProfile")
        Log.d("ProfileFragment", "Saving - Name: $name, Email: $email, Mobile: $mobile")

        // Create updated profile with new values
        val updatedProfile = currentProfile.copy(
            userName = name,
            userEmail = email,
            userMobile = mobile,
            dateOfBirth = dob,
            category = category,
            education = education
        )

        Log.d("ProfileFragment", "Updated profile: $updatedProfile")

        // Save to ViewModel
        sharedViewModel.saveUserProfile(updatedProfile)

        Toast.makeText(requireContext(), "Profile saved successfully! ✓", Toast.LENGTH_SHORT).show()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, day)
                val formattedDate = DateFormat.getDateInstance().format(selectedCalendar.time)
                binding.dobEditText.setText(formattedDate)
                Log.d("ProfileFragment", "Date selected: $formattedDate")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showCategoryDialog() {
        val categories = ExamCategory.values().map { it.displayName }.toTypedArray()
        val dialogBinding = DialogSelectCategoryBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.saveCategoryButton.setOnClickListener {
            val selectedId = dialogBinding.categoryRadioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedRadioButton = dialogBinding.root.findViewById<RadioButton>(selectedId)
                val categoryText = selectedRadioButton.text.toString()
                binding.categoryEditText.setText(categoryText)
                Log.d("ProfileFragment", "Category selected: $categoryText")
            } else {
                Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        dialogBinding.closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showEducationDialog() {
        val educationLevels = EducationLevel.values().map { it.displayName }.toTypedArray()
        val dialogBinding = DialogSelectEducationBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.saveEducationButton.setOnClickListener {
            val selectedId = dialogBinding.educationRadioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedRadioButton = dialogBinding.root.findViewById<RadioButton>(selectedId)
                val educationText = selectedRadioButton.text.toString()
                binding.educationEditText.setText(educationText)
                Log.d("ProfileFragment", "Education selected: $educationText")
            } else {
                Toast.makeText(requireContext(), "Please select education level", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
