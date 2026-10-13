package com.example.gymapp.ui.workout

import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.GymApp
import com.example.gymapp.data.GymRepository
import com.example.gymapp.data.GymSnapshot
import com.example.gymapp.domain.Exercise
import com.example.gymapp.domain.OverloadSuggestion
import com.example.gymapp.domain.ProgressiveOverload
import com.example.gymapp.domain.Session
import com.example.gymapp.domain.SetEntry
import com.example.gymapp.domain.SetPerformance
import com.example.gymapp.domain.bestE1rm
import com.example.gymapp.domain.epley1Rm
import com.example.gymapp.domain.normalizeKg
import com.example.gymapp.domain.recordHolderId
import com.example.gymapp.domain.summarizeSets
import com.example.gymapp.service.RestTimerController
import com.example.gymapp.service.RestTimerStore
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Draft(val weightKg: Double, val reps: Int)

data class WorkoutExerciseItem(
    val exercise: Exercise,
    val sets: List<SetEntry>,
    val suggestion: OverloadSuggestion,
    val lastSummary: String?,
    val recordSetId: Long?,
)

data class WorkoutUiState(
    val loading: Boolean = true,
    val session: Session? = null,
    val items: List<WorkoutExerciseItem> = emptyList(),
    val catalog: List<Exercise> = emptyList(),
)

sealed interface WorkoutEvent {
    data class PersonalRecord(val exerciseName: String, val e1rm: Double) : WorkoutEvent
}

class WorkoutViewModel(
    private val app: GymApp,
    private val sessionId: Long,
) : AndroidViewModel(app) {
    private val repository: GymRepository = app.repository
    private val _state = MutableStateFlow(WorkoutUiState())
    val state: StateFlow<WorkoutUiState> = _state
    private val _drafts = MutableStateFlow<Map<Long, Draft>>(emptyMap())
    val drafts: StateFlow<Map<Long, Draft>> = _drafts
    private val _restSeconds = MutableStateFlow(90)
    val restSeconds: StateFlow<Int> = _restSeconds
    private val _events = MutableSharedFlow<WorkoutEvent>(extraBufferCapacity = 8)
    val events: SharedFlow<WorkoutEvent> = _events
    private var latest: GymSnapshot? = null
    private val lastLogAt = mutableMapOf<Long, Long>()

    init {
        _restSeconds.value = RestTimerStore.readRestSeconds(app)
        viewModelScope.launch {
            repository.ensureSeeded()
            repository.observeGym().collect { snapshot ->
                latest = snapshot
                val next = snapshot.toWorkout(sessionId)
                syncDrafts(next.items)
                _state.value = next
            }
        }
    }

    fun setRestSeconds(seconds: Int) {
        _restSeconds.value = seconds
        RestTimerStore.saveRestSeconds(app, seconds)
    }

    fun nudgeWeight(exerciseId: Long, direction: Int) {
        val item = item(exerciseId) ?: return
        val step = if (item.exercise.incrementKg > 0) item.exercise.incrementKg else 2.5
        val draft = draftFor(item)
        val next = normalizeKg(draft.weightKg + direction * step).coerceIn(0.0, 999.0)
        _drafts.value = _drafts.value + (exerciseId to draft.copy(weightKg = next))
    }

    fun nudgeReps(exerciseId: Long, direction: Int) {
        val item = item(exerciseId) ?: return
        val draft = draftFor(item)
        val next = (draft.reps + direction).coerceIn(1, 50)
        _drafts.value = _drafts.value + (exerciseId to draft.copy(reps = next))
    }

    fun setWeight(exerciseId: Long, weightKg: Double) {
        val item = item(exerciseId) ?: return
        val draft = draftFor(item)
        _drafts.value = _drafts.value + (exerciseId to draft.copy(weightKg = normalizeKg(weightKg).coerceIn(0.0, 999.0)))
    }

    fun setReps(exerciseId: Long, reps: Int) {
        val item = item(exerciseId) ?: return
        val draft = draftFor(item)
        _drafts.value = _drafts.value + (exerciseId to draft.copy(reps = reps.coerceIn(1, 50)))
    }

    fun logSet(exerciseId: Long) {
        val now = SystemClock.elapsedRealtime()
        if (now - (lastLogAt[exerciseId] ?: 0L) < 350L) return
        lastLogAt[exerciseId] = now
        val item = item(exerciseId) ?: return
        val draft = draftFor(item)
        val weight = normalizeKg(draft.weightKg).coerceIn(0.0, 999.0)
        val reps = draft.reps.coerceIn(1, 50)
        val snapshot = latest ?: return
        viewModelScope.launch {
            val best = bestE1rm(
                snapshot.sets
                    .filter { it.exerciseId == exerciseId }
                    .map { SetPerformance(it.weightKg, it.reps) },
            )
            val mine = epley1Rm(weight, reps)
            repository.addSet(sessionId, exerciseId, weight, reps)
            if (mine != null && best != null && mine > best + 0.05) {
                _events.emit(WorkoutEvent.PersonalRecord(item.exercise.name, mine))
            }
            if (_state.value.session?.finishedAt == null) {
                RestTimerController.start(app, _restSeconds.value, sessionId)
            }
        }
    }

    fun addExercise(exerciseId: Long) {
        viewModelScope.launch { repository.addExerciseToSession(sessionId, exerciseId) }
    }

    fun removeExercise(exerciseId: Long) {
        viewModelScope.launch { repository.removeExercise(sessionId, exerciseId) }
    }

    fun updateSet(setId: Long, weightKg: Double, reps: Int) {
        viewModelScope.launch {
            repository.updateSet(setId, weightKg.coerceIn(0.0, 999.0), reps.coerceIn(1, 50))
        }
    }

    fun deleteSet(setId: Long) {
        viewModelScope.launch { repository.deleteSet(setId) }
    }

    suspend fun finishAndWait() {
        val session = _state.value.session ?: return
        if (session.finishedAt != null) return
        val empty = _state.value.items.all { it.sets.isEmpty() }
        withContext(NonCancellable) {
            if (empty) repository.deleteSession(session.id) else repository.finishSession(session.id)
        }
    }

    suspend fun discardIfEmpty() {
        val session = _state.value.session ?: return
        if (session.finishedAt != null) return
        if (_state.value.items.any { it.sets.isNotEmpty() }) return
        withContext(NonCancellable) {
            repository.deleteSession(session.id)
        }
    }

    suspend fun deleteSessionAndWait() {
        withContext(NonCancellable) {
            repository.deleteSession(sessionId)
        }
    }

    private fun item(exerciseId: Long) = _state.value.items.find { it.exercise.id == exerciseId }

    private fun draftFor(item: WorkoutExerciseItem): Draft {
        return _drafts.value[item.exercise.id] ?: Draft(item.suggestion.weightKg, item.suggestion.targetReps)
    }

    private fun syncDrafts(items: List<WorkoutExerciseItem>) {
        val current = _drafts.value.toMutableMap()
        var changed = false
        items.forEach { item ->
            if (item.exercise.id !in current) {
                current[item.exercise.id] = Draft(item.suggestion.weightKg, item.suggestion.targetReps)
                changed = true
            }
        }
        if (changed) _drafts.value = current
    }
}

private fun GymSnapshot.toWorkout(sessionId: Long): WorkoutUiState {
    val session = sessions.find { it.id == sessionId }
    val byId = exercises.associateBy { it.id }
    val sessionSets = sets.filter { it.sessionId == sessionId }
    val items = links
        .filter { it.sessionId == sessionId }
        .sortedBy { it.position }
        .mapNotNull { link ->
            val exercise = byId[link.exerciseId] ?: return@mapNotNull null
            val logged = sessionSets.filter { it.exerciseId == exercise.id }.sortedBy { it.completedAt }
            val previous = previousSessionSets(sessionId, exercise.id)
            val suggestion = ProgressiveOverload.suggest(
                lastSessionSets = previous,
                repMin = exercise.repMin,
                repMax = exercise.repMax,
                incrementKg = exercise.incrementKg,
                startKg = exercise.startKg,
            )
            WorkoutExerciseItem(
                exercise = exercise,
                sets = logged,
                suggestion = suggestion,
                lastSummary = previous.takeIf { it.isNotEmpty() }?.let {
                    summarizeSets(it, exercise.addedLoad)
                },
                recordSetId = recordHolderId(sets.filter { it.exerciseId == exercise.id }),
            )
        }
    return WorkoutUiState(
        loading = false,
        session = session,
        items = items,
        catalog = exercises.sortedWith(compareBy({ it.muscle.ordinal }, { it.name })),
    )
}

private fun GymSnapshot.previousSessionSets(currentSessionId: Long, exerciseId: Long): List<SetPerformance> {
    val candidates = sets.filter { it.exerciseId == exerciseId && it.sessionId != currentSessionId }
    if (candidates.isEmpty()) return emptyList()
    val startedAt = sessions.associate { it.id to it.startedAt }
    val lastSessionId = candidates.maxBy { startedAt[it.sessionId] ?: 0L }.sessionId
    return candidates
        .filter { it.sessionId == lastSessionId }
        .sortedBy { it.completedAt }
        .map { SetPerformance(it.weightKg, it.reps) }
}
