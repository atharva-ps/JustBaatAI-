package com.example.justbaatai // Corrected package

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.justbaatai.databinding.DialogSelectCategoryBinding
import com.example.justbaatai.databinding.DialogSelectEducationBinding
import com.example.justbaatai.databinding.FragmentProfileBinding
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
        setupClickListeners()
        observeViewModelData()
    }

    private fun setupClickListeners() {
        binding.dobEditText.isFocusable = false
        binding.dobEditText.setOnClickListener { showDatePickerDialog(binding.dobEditText) }

        binding.categoryEditText.isFocusable = false
        binding.categoryEditText.setOnClickListener { showCategoryDialog(binding.categoryEditText) }

        binding.educationEditText.isFocusable = false
        binding.educationEditText.setOnClickListener { showEducationDialog(binding.educationEditText) }

        binding.saveProfileButton.setOnClickListener {
            saveDataToViewModel()
            Toast.makeText(requireContext(), "Profile Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModelData() {
        sharedViewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.nameEditText.setText(name)
        }
        sharedViewModel.userEmail.observe(viewLifecycleOwner) { email ->
            binding.emailEditText.setText(email)
        }
        sharedViewModel.userMobile.observe(viewLifecycleOwner) { mobile ->
            binding.mobileEditText.setText(mobile)
        }
    }

    private fun saveDataToViewModel() {
        sharedViewModel.updateUserName(binding.nameEditText.text.toString())
        sharedViewModel.updateUserEmail(binding.emailEditText.text.toString())
        sharedViewModel.updateUserMobile(binding.mobileEditText.text.toString())
    }

    private fun showDatePickerDialog(dateField: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, day)
                dateField.setText(DateFormat.getDateInstance().format(selectedCalendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showCategoryDialog(categoryField: EditText) {
        val dialogBinding = DialogSelectCategoryBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogBinding.root).create()

        dialogBinding.saveCategoryButton.setOnClickListener {
            val selectedId = dialogBinding.categoryRadioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedRadioButton = dialogBinding.root.findViewById<RadioButton>(selectedId)
                categoryField.setText(selectedRadioButton.text)
            }
            dialog.dismiss()
        }
        dialogBinding.closeButton.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showEducationDialog(educationField: EditText) {
        val dialogBinding = DialogSelectEducationBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogBinding.root).create()

        dialogBinding.saveEducationButton.setOnClickListener {
            val selectedId = dialogBinding.educationRadioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedRadioButton = dialogBinding.root.findViewById<RadioButton>(selectedId)
                educationField.setText(selectedRadioButton.text)
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