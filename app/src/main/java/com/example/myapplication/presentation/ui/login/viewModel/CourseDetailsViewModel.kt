package com.example.learningdashboard.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningdashboard.data.repository.CourseRepository
import com.example.myapplication.domain.model.Course
import com.example.myapplication.domain.model.Lesson
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface CourseDetailsUiState {
    data object Loading : CourseDetailsUiState
    data object NotFound : CourseDetailsUiState
    data class Content(val course: Course, val lessons: List<Lesson>) : CourseDetailsUiState
}

class CourseDetailsViewModel(
    private val courseId: Int,
    private val repository: CourseRepository,
) : ViewModel() {

    val uiState: StateFlow<CourseDetailsUiState> = combine(
        repository.observeCourse(courseId),
        repository.observeLessons(courseId),
    ) { course, lessons ->
        if (course == null) CourseDetailsUiState.NotFound
        else CourseDetailsUiState.Content(course, lessons)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CourseDetailsUiState.Loading,
    )

    fun toggleLesson(lesson: Lesson) {
        viewModelScope.launch {
            repository.setLessonCompleted(courseId, lesson.position, !lesson.isCompleted)
        }
    }
}