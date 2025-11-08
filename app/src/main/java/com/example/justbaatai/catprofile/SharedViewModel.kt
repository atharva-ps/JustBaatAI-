package com.example.justbaatai.catprofile

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val gson = Gson()
    private val sharedPref = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
    private val PROFILE_KEY = "user_profile_data"

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> = _userProfile

    private val _isSaving = MutableLiveData<Boolean>(false)
    val isSaving: LiveData<Boolean> = _isSaving

    init {
        Log.d("SharedViewModel", "ViewModel initialized")
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val profileJson = sharedPref.getString(PROFILE_KEY, null)
                    val profile = if (profileJson != null) {
                        Log.d("SharedViewModel", "Loading profile from SharedPreferences: $profileJson")
                        gson.fromJson(profileJson, UserProfile::class.java)
                    } else {
                        Log.d("SharedViewModel", "No profile found, creating guest profile")
                        createGuestProfile()
                    }
                    Log.d("SharedViewModel", "Profile loaded: $profile")
                    _userProfile.postValue(profile)
                } catch (e: Exception) {
                    Log.e("SharedViewModel", "Error loading profile", e)
                    _userProfile.postValue(createGuestProfile())
                }
            }
        }
    }

    fun saveUserProfile(profile: UserProfile) {
        Log.d("SharedViewModel", "Saving profile: $profile")
        _isSaving.value = true

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val profileJson = gson.toJson(profile)
                    Log.d("SharedViewModel", "Profile JSON: $profileJson")

                    sharedPref.edit().putString(PROFILE_KEY, profileJson).apply()

                    Log.d("SharedViewModel", "Profile saved successfully")

                    _userProfile.postValue(profile)
                    _isSaving.postValue(false)
                } catch (e: Exception) {
                    Log.e("SharedViewModel", "Error saving profile", e)
                    _isSaving.postValue(false)
                }
            }
        }
    }

    fun updateUserName(name: String) {
        val currentProfile = _userProfile.value ?: createGuestProfile()
        saveUserProfile(currentProfile.copy(userName = name))
    }

    fun updateUserEmail(email: String) {
        val currentProfile = _userProfile.value ?: createGuestProfile()
        saveUserProfile(currentProfile.copy(userEmail = email))
    }

    fun updateUserMobile(mobile: String) {
        val currentProfile = _userProfile.value ?: createGuestProfile()
        saveUserProfile(currentProfile.copy(userMobile = mobile))
    }

    fun updateDateOfBirth(dob: String) {
        val currentProfile = _userProfile.value ?: createGuestProfile()
        saveUserProfile(currentProfile.copy(dateOfBirth = dob))
    }

    fun updateCategory(category: String) {
        val currentProfile = _userProfile.value ?: createGuestProfile()
        saveUserProfile(currentProfile.copy(category = category))
    }

    fun updateEducation(education: String) {
        val currentProfile = _userProfile.value ?: createGuestProfile()
        saveUserProfile(currentProfile.copy(education = education))
    }

    fun updateProfilePicture(picturePath: String?) {
        val currentProfile = _userProfile.value ?: createGuestProfile()
        saveUserProfile(currentProfile.copy(profilePicture = picturePath))
    }

    fun createGuestProfile(): UserProfile {
        val guestProfile = UserProfile(
            userId = "guest_${System.currentTimeMillis()}",
            userName = "Guest User",
            isGuest = true
        )
        Log.d("SharedViewModel", "Guest profile created: $guestProfile")
        return guestProfile
    }

    fun getInitials(name: String): String {
        return name.split(" ")
            .mapNotNull { it.firstOrNull() }
            .joinToString("")
            .take(2)
            .uppercase()
    }
}
