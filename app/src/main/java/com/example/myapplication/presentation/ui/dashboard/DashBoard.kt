package com.example.learningdashboard.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningdashboard.data.repository.CourseRepository
import com.example.myapplication.domain.model.Course
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data object Empty : DashboardUiState
    data class Success(val courses: List<Course>, val warning: String? = null) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

class DashboardViewModel(private val repository: CourseRepository) : ViewModel() {

    private data class SyncState(val isSyncing: Boolean = true, val error: String? = null)

    private val sync = MutableStateFlow(SyncState())

    /**
     * Room is the source of truth. The UI reads Room and the sync state, then
     * decides what to show. Cached data always wins over an error.
     */
    val uiState: StateFlow<DashboardUiState> = combine(
        repository.observeCourses(),
        sync,
    ) { courses, syncState ->
        when {
            courses.isNotEmpty() -> DashboardUiState.Success(
                courses = courses,
                warning = if (syncState.error != null) "You're offline. Showing saved courses." else null,
            )
            syncState.isSyncing -> DashboardUiState.Loading
            syncState.error != null -> DashboardUiState.Error("Couldn't load courses. Check your connection.")
            else -> DashboardUiState.Empty
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState.Loading,
    )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            sync.update { it.copy(isSyncing = true, error = null) }
            val result = repository.refresh()
            sync.value = SyncState(
                isSyncing = false,
                error = if (result.isFailure) "Could not refresh courses" else null,
            )
        }
    }
}