package com.example.justbaatai // Corrected package

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    private val _userName = MutableLiveData("Aditi Singh")
    val userName: LiveData<String> = _userName

    private val _userEmail = MutableLiveData("singhaditi80690@gmail.com")
    val userEmail: LiveData<String> = _userEmail

    private val _userMobile = MutableLiveData("919305611494")
    val userMobile: LiveData<String> = _userMobile

    fun updateUserName(name: String) {
        _userName.value = name
    }
    fun updateUserEmail(email: String) {
        _userEmail.value = email
    }
    fun updateUserMobile(mobile: String) {
        _userMobile.value = mobile
    }
}