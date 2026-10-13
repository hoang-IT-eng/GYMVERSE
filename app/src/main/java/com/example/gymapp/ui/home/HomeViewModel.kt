package com.example.gymapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.data.GymRepository
import com.example.gymapp.data.GymSnapshot
import com.example.gymapp.domain.pointsForExercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ActiveSessionUi(
    val sessionId: Long,
    val startedAt: Long,
    val setCount: Int,
    val exerciseNames: List<String>,
)

data class RecentSessionUi(
    val sessionId: Long,
    val startedAt: Long,
    val finishedAt: Long?,
    val setCount: Int,
    val title: String,
)

data class ExerciseProgressUi(
    val exerciseId: Long,
    val name: String,
    val latestE1rm: Double?,
    val deltaE1rm: Double?,
    val bestReps: Int,
    val sparkline: List<Float>,
    val lastTrainedAt: Long,
)

data class HomeUiState(
    val loading: Boolean = true,
    val active: ActiveSessionUi? = null,
    val recent: List<RecentSessionUi> = emptyList(),
    val progress: List<ExerciseProgressUi> = emptyList(),
)

class HomeViewModel(private val repository: GymRepository) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state
    private var latest: GymSnapshot? = null

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            repository.observeGym().collect { snapshot ->
                latest = snapshot
                _state.value = snapshot.toHome()
            }
        }
    }

    suspend fun startOrResume(): Long {
        repository.ensureSeeded()
        val active = latest?.sessions
            ?.filter { it.finishedAt == null }
            ?.maxByOrNull { it.startedAt }
        return active?.id ?: repository.startSession()
    }
}

private fun GymSnapshot.toHome(): HomeUiState {
    val activeSession = sessions.filter { it.finishedAt == null }.maxByOrNull { it.startedAt }
    val active = activeSession?.let { session ->
        ActiveSessionUi(
            sessionId = session.id,
            startedAt = session.startedAt,
            setCount = sets.count { it.sessionId == session.id },
            exerciseNames = exerciseNames(session.id),
        )
    }
    val recent = sessions
        .filter { it.id != activeSession?.id }
        .sortedByDescending { it.startedAt }
        .take(20)
        .map { session ->
            val names = exerciseNames(session.id)
            val shown = names.take(3)
            val extra = names.size - shown.size
            val title = when {
                shown.isEmpty() -> "Chưa có set"
                extra > 0 -> shown.joinToString(", ") + " +$extra"
                else -> shown.joinToString(", ")
            }
            RecentSessionUi(
                sessionId = session.id,
                startedAt = session.startedAt,
                finishedAt = session.finishedAt,
                setCount = sets.count { it.sessionId == session.id },
                title = title,
            )
        }
    val progress = exercises.mapNotNull { exercise ->
        val points = pointsForExercise(sets, sessions, exercise.id)
        if (points.isEmpty()) return@mapNotNull null
        val latestPoint = points.last()
        val previousE1rm = points.dropLast(1).mapNotNull { it.bestE1rm }.lastOrNull()
        val useE1rm = latestPoint.bestE1rm != null
        val series = points.mapNotNull { point ->
            if (useE1rm) point.bestE1rm else point.bestReps.toDouble()
        }.takeLast(8).map { it.toFloat() }
        ExerciseProgressUi(
            exerciseId = exercise.id,
            name = exercise.name,
            latestE1rm = latestPoint.bestE1rm,
            deltaE1rm = if (latestPoint.bestE1rm != null && previousE1rm != null) {
                latestPoint.bestE1rm - previousE1rm
            } else {
                null
            },
            bestReps = latestPoint.bestReps,
            sparkline = series,
            lastTrainedAt = latestPoint.startedAt,
        )
    }.sortedByDescending { it.lastTrainedAt }
    return HomeUiState(
        loading = false,
        active = active,
        recent = recent,
        progress = progress,
    )
}

private fun GymSnapshot.exerciseNames(sessionId: Long): List<String> {
    val byId = exercises.associateBy { it.id }
    val trained = sets.filter { it.sessionId == sessionId }.map { it.exerciseId }.toSet()
    return links
        .filter { it.sessionId == sessionId && it.exerciseId in trained }
        .sortedBy { it.position }
        .mapNotNull { byId[it.exerciseId]?.name }
}
