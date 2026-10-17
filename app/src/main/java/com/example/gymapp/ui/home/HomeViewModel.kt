package com.example.gymapp.ui.home

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.GymApp
import com.example.gymapp.data.GymRepository
import com.example.gymapp.data.GymSnapshot
import com.example.gymapp.domain.DayCheckItem
import com.example.gymapp.domain.Exercise
import com.example.gymapp.domain.IronLogCatalog
import com.example.gymapp.domain.MuscleRecoveryItem
import com.example.gymapp.domain.ProgressiveOverload
import com.example.gymapp.domain.RoutineDaySpec
import com.example.gymapp.domain.RoutinePlan
import com.example.gymapp.domain.SetPerformance
import com.example.gymapp.domain.toPlan
import com.example.gymapp.domain.epley1Rm
import com.example.gymapp.domain.formatEstimate
import com.example.gymapp.domain.formatLoad
import com.example.gymapp.domain.pointsForExercise
import com.example.gymapp.service.RestTimerStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

data class ActiveSessionUi(
    val sessionId: Long,
    val startedAt: Long,
    val setCount: Int,
    val volumeKg: Double,
    val exerciseNames: List<String>,
)

data class RecentSessionUi(
    val sessionId: Long,
    val startedAt: Long,
    val finishedAt: Long?,
    val setCount: Int,
    val volumeKg: Double,
    val title: String,
    val exercisesSummary: List<String>,
)

data class ExerciseProgressUi(
    val exerciseId: Long,
    val name: String,
    val vietnameseName: String,
    val muscleLabel: String,
    val latestE1rm: Double?,
    val deltaE1rm: Double?,
    val bestReps: Int,
    val sparkline: List<Float>,
    val lastTrainedAt: Long,
)

data class HeroExercisePreview(
    val order: Int,
    val exerciseId: Long?,
    val name: String,
    val targetLine: String,
    val targetSets: Int,
)

data class HomeUiState(
    val loading: Boolean = true,
    val athleteName: String = "Minh Quân",
    val activeRoutine: RoutinePlan = IronLogCatalog.routines.first(),
    val heroTitle: String = "PUSH DAY A",
    val heroFocus: String = "Ngực • Vai • Tay Sau",
    val heroPreviews: List<HeroExercisePreview> = emptyList(),
    val active: ActiveSessionUi? = null,
    val recent: List<RecentSessionUi> = emptyList(),
    val progress: List<ExerciseProgressUi> = emptyList(),
    val streakDays: Int = 0,
    val weekStrip: List<DayCheckItem> = emptyList(),
    val recovery: List<MuscleRecoveryItem> = emptyList(),
    val weeklyVolumeKg: Double = 0.0,
    val weeklyVolumeDeltaPct: Int? = null,
    val weeklyPrCount: Int = 0,
    val topPrLine: String = "Chưa có PR tuần này",
    val catalog: List<Exercise> = emptyList(),
    val customPlans: List<RoutinePlan> = emptyList(),
)

class HomeViewModel(private val app: GymApp) : AndroidViewModel(app) {
    private val repository: GymRepository = app.repository
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state
    private var latest: GymSnapshot? = null

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            repository.observeGym().collect { snapshot ->
                latest = snapshot
                _state.value = snapshot.toHome(app)
            }
        }
    }

    suspend fun startOrResume(): Long {
        repository.ensureSeeded()
        val active = latest?.sessions
            ?.filter { it.finishedAt == null }
            ?.maxByOrNull { it.startedAt }
        if (active != null) return active.id
        val ids = _state.value.heroPreviews.mapNotNull { it.exerciseId }
        return if (ids.isNotEmpty()) {
            repository.startSessionWithExerciseIds(ids)
        } else {
            repository.startSessionWithExercises(_state.value.heroPreviews.map { it.name })
        }
    }

    suspend fun startRoutineDay(exerciseNames: List<String>): Long {
        return startRoutineDay(
            RoutineDaySpec(
                dayNumber = 1,
                title = "",
                subtitle = "",
                targetRpe = "",
                exerciseNames = exerciseNames,
            ),
        )
    }

    suspend fun startRoutineDay(day: RoutineDaySpec): Long {
        repository.ensureSeeded()
        val active = latest?.sessions
            ?.filter { it.finishedAt == null }
            ?.maxByOrNull { it.startedAt }
        if (active != null) {
            val existingCount = latest?.sets?.count { it.sessionId == active.id } ?: 0
            if (existingCount == 0) {
                repository.deleteSession(active.id)
            } else {
                return active.id
            }
        }
        return if (day.exerciseIds.isNotEmpty()) {
            repository.startSessionWithExerciseIds(day.exerciseIds)
        } else {
            repository.startSessionWithExercises(day.exerciseNames)
        }
    }
}

private fun GymSnapshot.toHome(app: GymApp): HomeUiState {
    val activeSession = sessions.filter { it.finishedAt == null }.maxByOrNull { it.startedAt }
    val active = activeSession?.let { session ->
        val sessionSets = sets.filter { it.sessionId == session.id }
        ActiveSessionUi(
            sessionId = session.id,
            startedAt = session.startedAt,
            setCount = sessionSets.size,
            volumeKg = sessionSets.sumOf { it.weightKg * it.reps },
            exerciseNames = exerciseNames(session.id),
        )
    }

    val recent = sessions
        .filter { it.id != activeSession?.id }
        .sortedByDescending { it.startedAt }
        .take(30)
        .map { session ->
            val names = exerciseNames(session.id)
            val shown = names.take(3)
            val extra = names.size - shown.size
            val title = when {
                shown.isEmpty() -> "Buổi tập tự do"
                extra > 0 -> shown.joinToString(", ") + " +$extra"
                else -> shown.joinToString(", ")
            }
            val sessionSets = sets.filter { it.sessionId == session.id }
            RecentSessionUi(
                sessionId = session.id,
                startedAt = session.startedAt,
                finishedAt = session.finishedAt,
                setCount = sessionSets.size,
                volumeKg = sessionSets.sumOf { it.weightKg * it.reps },
                title = title,
                exercisesSummary = names,
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
        val meta = IronLogCatalog.metaFor(exercise)
        ExerciseProgressUi(
            exerciseId = exercise.id,
            name = exercise.name,
            vietnameseName = meta.vietnameseName,
            muscleLabel = exercise.muscle.label,
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

    val byId = exercises.associateBy { it.id }
    val byName = exercises.associateBy { it.name }
    val customPlans = customRoutines.map { it.toPlan(byId) }
    val routineId = RestTimerStore.readActiveRoutineId(app)
    val routine = customPlans.find { it.id == routineId }
        ?: IronLogCatalog.routines.find { it.id == routineId }
        ?: customPlans.firstOrNull()
        ?: IronLogCatalog.routines.first()
    val workoutDays = routine.days.filter { !it.isRest }
    val daySpec = workoutDays.firstOrNull() ?: routine.days.firstOrNull()
    val startedAtBySession = sessions.associate { it.id to it.startedAt }

    val heroSources: List<Exercise?> = if (daySpec == null) {
        emptyList()
    } else if (daySpec.exerciseIds.isNotEmpty()) {
        daySpec.exerciseIds.map { byId[it] }
    } else {
        daySpec.exerciseNames.map { byName[it] }
    }
    val heroPreviews = heroSources.mapIndexed { idx, ex ->
        if (ex == null) {
            val fallbackName = daySpec?.exerciseNames?.getOrNull(idx) ?: "Bài tập"
            HeroExercisePreview(idx + 1, null, fallbackName, "Mục tiêu: 8–10 reps @ RIR 2", 3)
        } else {
            val exSets = sets.filter { it.exerciseId == ex.id }
            val lastSessionId = exSets.maxByOrNull { startedAtBySession[it.sessionId] ?: 0L }?.sessionId
            val lastPerf = if (lastSessionId != null) {
                exSets.filter { it.sessionId == lastSessionId }
                    .sortedBy { it.completedAt }
                    .map { SetPerformance(it.weightKg, it.reps) }
            } else {
                emptyList()
            }
            val suggestion = ProgressiveOverload.suggest(
                lastSessionSets = lastPerf,
                repMin = ex.repMin,
                repMax = ex.repMax,
                incrementKg = ex.incrementKg,
                startKg = ex.startKg,
            )
            HeroExercisePreview(
                order = idx + 1,
                exerciseId = ex.id,
                name = ex.name,
                targetLine = "Mục tiêu: ${formatLoad(suggestion.weightKg, ex.addedLoad)} × ${ex.repMin}-${ex.repMax} @ RIR 2",
                targetSets = if (idx == 0) 4 else 3,
            )
        }
    }

    val (streak, weekStrip) = IronLogCatalog.computeWeekStripAndStreak(sessions, sets)
    val recovery = IronLogCatalog.computeRecoveryStatus(exercises, sets)

    // Weekly volume & PR stats
    val zone = ZoneId.systemDefault()
    val today = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(zone).toLocalDate()
    val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val thisWeekStart = monday.atStartOfDay(zone).toInstant().toEpochMilli()
    val prevWeekStart = monday.minusWeeks(1).atStartOfDay(zone).toInstant().toEpochMilli()

    val thisWeekSessionIds = sessions.filter { it.startedAt >= thisWeekStart }.map { it.id }.toSet()
    val prevWeekSessionIds = sessions.filter { it.startedAt in prevWeekStart until thisWeekStart }.map { it.id }.toSet()

    val thisWeekVolume = sets.filter { it.sessionId in thisWeekSessionIds }.sumOf { it.weightKg * it.reps }
    val prevWeekVolume = sets.filter { it.sessionId in prevWeekSessionIds }.sumOf { it.weightKg * it.reps }
    val deltaPct = if (prevWeekVolume > 0.0) {
        (((thisWeekVolume - prevWeekVolume) / prevWeekVolume) * 100.0).toInt()
    } else {
        null
    }

    // Count exercises whose all-time best e1RM occurred this week
    var prCount = 0
    var topPrExercise: Pair<String, Double>? = null
    for (ex in exercises) {
        val exSets = sets.filter { it.exerciseId == ex.id }
        val scored = exSets.mapNotNull { s ->
            epley1Rm(s.weightKg, s.reps)?.let { Triple(s.sessionId, s.completedAt, it) }
        }
        if (scored.isEmpty()) continue
        val maxEst = scored.maxOf { it.third }
        val holder = scored.filter { it.third >= maxEst - 0.05 }.minByOrNull { it.second }
        if (holder != null && holder.first in thisWeekSessionIds) {
            prCount++
        }
        if (topPrExercise == null || maxEst > topPrExercise.second) {
            topPrExercise = ex.name to maxEst
        }
    }

    val topPrLine = if (topPrExercise != null) {
        "${topPrExercise.first} ${formatEstimate(topPrExercise.second)}kg e1RM"
    } else {
        "Ghi hiệp để mở khóa PR"
    }

    val titleParts = daySpec?.title?.split(" - ").orEmpty()
    val heroTitle = titleParts.firstOrNull()?.uppercase()?.ifBlank { null } ?: routine.name.uppercase()
    val heroFocus = titleParts.getOrNull(1) ?: routine.subtitle

    return HomeUiState(
        loading = false,
        athleteName = RestTimerStore.readAthleteName(app),
        activeRoutine = routine,
        heroTitle = heroTitle,
        heroFocus = heroFocus,
        heroPreviews = heroPreviews,
        active = active,
        recent = recent,
        progress = progress,
        streakDays = streak,
        weekStrip = weekStrip,
        recovery = recovery,
        weeklyVolumeKg = thisWeekVolume,
        weeklyVolumeDeltaPct = deltaPct,
        weeklyPrCount = prCount,
        topPrLine = topPrLine,
        catalog = exercises,
        customPlans = customPlans,
    )
}

private fun GymSnapshot.exerciseNames(sessionId: Long): List<String> {
    val byId = exercises.associateBy { it.id }
    val trained = sets.filter { it.sessionId == sessionId }.map { it.exerciseId }.toSet()
    val linked = links
        .filter { it.sessionId == sessionId }
        .sortedBy { it.position }
        .mapNotNull { byId[it.exerciseId]?.name }
    val trainedNames = links
        .filter { it.sessionId == sessionId && it.exerciseId in trained }
        .sortedBy { it.position }
        .mapNotNull { byId[it.exerciseId]?.name }
    return trainedNames.ifEmpty { linked }
}
