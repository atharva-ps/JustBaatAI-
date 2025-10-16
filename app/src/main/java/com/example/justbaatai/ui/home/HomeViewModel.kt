package com.example.justbaatai.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.justbaatai.HomeGridItem
import com.example.justbaatai.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository // Hilt will provide this for you
) : ViewModel() {

    private val _gridItems = MutableLiveData<List<HomeGridItem>>()
    val gridItems: LiveData<List<HomeGridItem>> = _gridItems

    // Change the function name to reflect its purpose
    fun loadInitialData() {
        viewModelScope.launch {
            // Get the list of items from the repository
            val items = homeRepository.getHomeGridItems()
            _gridItems.value = items
        }
    }
}