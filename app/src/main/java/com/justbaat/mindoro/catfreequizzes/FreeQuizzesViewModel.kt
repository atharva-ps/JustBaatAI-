package com.justbaat.mindoro.catfreequizzes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FreeQuizzesViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<QuizUiState>()
    val uiState: LiveData<QuizUiState> = _uiState

    private val _categories = MutableLiveData<List<QuizCategory>>()
    val categories: LiveData<List<QuizCategory>> = _categories

    private val _filteredTests = MutableLiveData<List<LiveTest>>()
    val filteredTests: LiveData<List<LiveTest>> = _filteredTests

    private val _syncStatus = MutableLiveData<SyncStatus>()
    val syncStatus: LiveData<SyncStatus> = _syncStatus

    private var currentCategoryId: String? = null

    init {
        loadQuizData()
    }

    fun loadQuizData() {
        _uiState.value = QuizUiState.Loading

        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    repository.loadQuizData()
                }

                _categories.value = data.categories
                _filteredTests.value = emptyList()

                _uiState.value = QuizUiState.Success(
                    categories = data.categories,
                    tests = emptyList()
                )
            } catch (e: Exception) {
                _uiState.value = QuizUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun selectCategory(categoryId: String?) {
        currentCategoryId = categoryId

        viewModelScope.launch {
            val tests = withContext(Dispatchers.IO) {
                if (categoryId != null) {
                    repository.getTestsByCategory(categoryId)
                } else {
                    emptyList()
                }
            }
            _filteredTests.value = tests
        }
    }

    fun syncQuizData() {
        _syncStatus.value = SyncStatus.Syncing

        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.syncQuizDataFromServer()
            }

            if (success) {
                _syncStatus.value = SyncStatus.Success
                loadQuizData() // Reload data after successful sync
            } else {
                _syncStatus.value = SyncStatus.Failed("Failed to sync data")
            }
        }
    }

    fun getLastSyncTime(): Long {
        return repository.getLastSyncTime()
    }

    fun searchTests(query: String) {
        viewModelScope.launch {
            val tests = withContext(Dispatchers.IO) {
                repository.searchTests(query)
            }
            _filteredTests.value = tests
        }
    }

    fun retry() {
        loadQuizData()
    }
}

sealed class SyncStatus {
    object Idle : SyncStatus()
    object Syncing : SyncStatus()
    object Success : SyncStatus()
    data class Failed(val message: String) : SyncStatus()
}
