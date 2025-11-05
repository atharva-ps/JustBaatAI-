package com.example.justbaatai.catstudynotes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StudyNotesViewModel : ViewModel() {

    // Use LiveData to hold the list of notes. The Fragment will observe this.
    private val _studyNotes = MutableLiveData<List<StudyNote>>()
    val studyNotes: LiveData<List<StudyNote>> = _studyNotes

    // This function will be called to load the data.
    // For now, it just creates the same sample data.
    fun loadStudyNotes() {
        val sampleNotes = listOf(
            StudyNote(1, "Chapter 1: History", "A look at the origins of the subject."),
            StudyNote(2, "Chapter 2: Core Concepts", "Understanding the fundamental principles."),
            StudyNote(3, "Chapter 3: Advanced Topics", "Exploring more complex ideas and theories.")
        )
        _studyNotes.value = sampleNotes
    }
}