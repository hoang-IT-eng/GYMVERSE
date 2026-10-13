package com.example.gymapp.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.data.GymRepository
import com.example.gymapp.domain.Exercise
import com.example.gymapp.domain.ProgressPoint
import com.example.gymapp.domain.pointsForExercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProgressUiState(
    val loading: Boolean = true,
    val exercise: Exercise? = null,
    val points: List<ProgressPoint> = emptyList(),
)

class ProgressViewModel(
    private val repository: GymRepository,
    private val exerciseId: Long,
) : ViewModel() {
    private val _state = MutableStateFlow(ProgressUiState())
    val state: StateFlow<ProgressUiState> = _state

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            repository.observeGym().collect { snapshot ->
                _state.value = ProgressUiState(
                    loading = false,
                    exercise = snapshot.exercises.find { it.id == exerciseId },
                    points = pointsForExercise(snapshot.sets, snapshot.sessions, exerciseId),
                )
            }
        }
    }
}
