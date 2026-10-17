package com.example.gymapp.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.data.GymRepository
import com.example.gymapp.domain.Exercise
import com.example.gymapp.domain.OverloadSuggestion
import com.example.gymapp.domain.ProgressPoint
import com.example.gymapp.domain.ProgressiveOverload
import com.example.gymapp.domain.SetPerformance
import com.example.gymapp.domain.pointsForExercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProgressUiState(
    val loading: Boolean = true,
    val exercise: Exercise? = null,
    val catalog: List<Exercise> = emptyList(),
    val points: List<ProgressPoint> = emptyList(),
    val suggestion: OverloadSuggestion? = null,
)

class ProgressViewModel(
    private val repository: GymRepository,
    initialExerciseId: Long,
) : ViewModel() {
    private val selectedId = MutableStateFlow(initialExerciseId)
    private val _state = MutableStateFlow(ProgressUiState())
    val state: StateFlow<ProgressUiState> = _state

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            kotlinx.coroutines.flow.combine(
                repository.observeGym(),
                selectedId,
            ) { snapshot, exId ->
                val exercise = snapshot.exercises.find { it.id == exId } ?: snapshot.exercises.firstOrNull()
                val pts = if (exercise != null) {
                    pointsForExercise(snapshot.sets, snapshot.sessions, exercise.id)
                } else {
                    emptyList()
                }
                val lastSets = if (exercise != null) {
                    val exSets = snapshot.sets.filter { it.exerciseId == exercise.id }
                    val startedAt = snapshot.sessions.associate { it.id to it.startedAt }
                    val lastSessionId = exSets.maxByOrNull { startedAt[it.sessionId] ?: 0L }?.sessionId
                    if (lastSessionId != null) {
                        exSets.filter { it.sessionId == lastSessionId }
                            .sortedBy { it.completedAt }
                            .map { SetPerformance(it.weightKg, it.reps) }
                    } else {
                        emptyList()
                    }
                } else {
                    emptyList()
                }
                val suggestion = exercise?.let {
                    ProgressiveOverload.suggest(
                        lastSessionSets = lastSets,
                        repMin = it.repMin,
                        repMax = it.repMax,
                        incrementKg = it.incrementKg,
                        startKg = it.startKg,
                    )
                }
                ProgressUiState(
                    loading = false,
                    exercise = exercise,
                    catalog = snapshot.exercises,
                    points = pts,
                    suggestion = suggestion,
                )
            }.collect { _state.value = it }
        }
    }

    fun selectExercise(id: Long) {
        selectedId.value = id
    }
}
