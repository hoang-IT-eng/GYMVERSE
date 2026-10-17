package com.example.gymapp.data

import com.example.gymapp.domain.CustomRoutine
import com.example.gymapp.domain.CustomRoutineDay
import com.example.gymapp.domain.Exercise
import com.example.gymapp.domain.MuscleGroup
import com.example.gymapp.domain.Session
import com.example.gymapp.domain.SessionLink
import com.example.gymapp.domain.SetEntry
import com.example.gymapp.domain.normalizeKg
import com.example.gymapp.domain.parseMuscles
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class GymSnapshot(
    val exercises: List<Exercise>,
    val sessions: List<Session>,
    val links: List<SessionLink>,
    val sets: List<SetEntry>,
    val customRoutines: List<CustomRoutine> = emptyList(),
)

data class RoutineDayDraft(
    val title: String,
    val isRest: Boolean,
    val exerciseIds: List<Long>,
)

class GymRepository(private val dao: GymDao) {
    private val seedLock = Mutex()
    private var seeded = false

    suspend fun ensureSeeded() {
        if (seeded) return
        seedLock.withLock {
            if (seeded) return@withLock
            if (dao.countExercises() == 0) {
                dao.insertExercises(ExerciseSeed.entities())
            }
            seeded = true
        }
    }

    fun observeGym(): Flow<GymSnapshot> {
        val training = combine(
            dao.observeExercises(),
            dao.observeSessions(),
            dao.observeLinks(),
            dao.observeSets(),
        ) { exercises, sessions, links, sets ->
            GymSnapshot(
                exercises = exercises.map { it.toDomain() },
                sessions = sessions.map { it.toDomain() },
                links = links.map { SessionLink(it.sessionId, it.exerciseId, it.position) },
                sets = sets.map { it.toDomain() },
            )
        }
        val routines = combine(
            dao.observeCustomRoutines(),
            dao.observeCustomDays(),
            dao.observeCustomDayExercises(),
        ) { routineRows, dayRows, exerciseRows ->
            routineRows.sortedByDescending { it.createdAt }.map { routine ->
                val days = dayRows.filter { it.routineId == routine.id }.sortedBy { it.position }
                CustomRoutine(
                    id = routine.id,
                    name = routine.name,
                    days = days.map { day ->
                        CustomRoutineDay(
                            id = day.id,
                            title = day.title,
                            isRest = day.isRest,
                            exerciseIds = exerciseRows
                                .filter { it.dayId == day.id }
                                .sortedBy { it.position }
                                .map { it.exerciseId },
                        )
                    },
                )
            }
        }
        return combine(training, routines) { snapshot, custom ->
            snapshot.copy(customRoutines = custom)
        }
    }

    suspend fun startSession(now: Long = System.currentTimeMillis()): Long {
        return dao.insertSession(SessionEntity(startedAt = now))
    }

    suspend fun startSessionWithExercises(
        exerciseNames: List<String>,
        now: Long = System.currentTimeMillis(),
    ): Long {
        ensureSeeded()
        val sessionId = dao.insertSession(SessionEntity(startedAt = now))
        if (exerciseNames.isNotEmpty()) {
            val byName = dao.getAllExercises().associateBy { it.name }
            exerciseNames.forEach { name ->
                val ex = byName[name]
                if (ex != null) {
                    addExerciseToSession(sessionId, ex.id)
                }
            }
        }
        return sessionId
    }

    suspend fun startSessionWithExerciseIds(
        exerciseIds: List<Long>,
        now: Long = System.currentTimeMillis(),
    ): Long {
        ensureSeeded()
        val sessionId = dao.insertSession(SessionEntity(startedAt = now))
        val known = dao.getAllExercises().map { it.id }.toSet()
        exerciseIds.distinct().filter { it in known }.forEach { id ->
            addExerciseToSession(sessionId, id)
        }
        return sessionId
    }

    suspend fun saveCustomRoutine(existingId: Long?, name: String, days: List<RoutineDayDraft>): Long {
        ensureSeeded()
        val routineId = if (existingId == null) {
            dao.insertCustomRoutine(
                CustomRoutineEntity(name = name.trim(), createdAt = System.currentTimeMillis()),
            )
        } else {
            dao.renameCustomRoutine(existingId, name.trim())
            dao.deleteDaysForRoutine(existingId)
            existingId
        }
        days.forEachIndexed { index, day ->
            val dayId = dao.insertCustomDay(
                CustomRoutineDayEntity(
                    routineId = routineId,
                    position = index,
                    title = day.title.trim().ifBlank { if (day.isRest) "Ngày nghỉ" else "Ngày ${index + 1}" },
                    isRest = day.isRest,
                ),
            )
            if (!day.isRest) {
                val links = day.exerciseIds.distinct().mapIndexed { position, exerciseId ->
                    CustomRoutineExerciseEntity(dayId = dayId, exerciseId = exerciseId, position = position)
                }
                if (links.isNotEmpty()) dao.insertCustomDayExercises(links)
            }
        }
        return routineId
    }

    suspend fun deleteCustomRoutine(id: Long) {
        dao.deleteCustomRoutine(id)
    }

    suspend fun createCustomExercise(
        name: String,
        muscle: MuscleGroup,
        repMin: Int = 8,
        repMax: Int = 12,
        incrementKg: Double = 2.5,
        startKg: Double = 20.0,
    ): Long {
        ensureSeeded()
        return dao.insertExercise(
            ExerciseEntity(
                name = name.trim(),
                muscle = muscle.name,
                secondaryMuscles = "",
                repMin = repMin.coerceIn(1, 30),
                repMax = repMax.coerceIn(repMin, 50),
                incrementKg = incrementKg,
                startKg = startKg,
                addedLoad = false,
            ),
        )
    }

    suspend fun finishSession(id: Long, now: Long = System.currentTimeMillis()) {
        dao.finishSession(id, now)
    }

    suspend fun deleteSession(id: Long) {
        dao.deleteSession(id)
    }

    suspend fun addExerciseToSession(sessionId: Long, exerciseId: Long) {
        if (dao.countLink(sessionId, exerciseId) > 0) return
        val position = dao.maxPosition(sessionId) + 1
        dao.insertSessionExercise(
            SessionExerciseEntity(
                sessionId = sessionId,
                exerciseId = exerciseId,
                position = position,
            ),
        )
    }

    suspend fun removeExercise(sessionId: Long, exerciseId: Long) {
        dao.deleteSetsForExercise(sessionId, exerciseId)
        dao.deleteLink(sessionId, exerciseId)
    }

    suspend fun addSet(sessionId: Long, exerciseId: Long, weightKg: Double, reps: Int): Long {
        val number = dao.countSets(sessionId, exerciseId) + 1
        return dao.insertSet(
            SetEntity(
                sessionId = sessionId,
                exerciseId = exerciseId,
                setNumber = number,
                weightKg = normalizeKg(weightKg),
                reps = reps,
                completedAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun updateSet(id: Long, weightKg: Double, reps: Int) {
        dao.updateSet(id, normalizeKg(weightKg), reps)
    }

    suspend fun deleteSet(id: Long) {
        dao.deleteSet(id)
    }
}

private fun ExerciseEntity.toDomain(): Exercise {
    return Exercise(
        id = id,
        name = name,
        muscle = MuscleGroup.entries.firstOrNull { it.name == muscle } ?: MuscleGroup.CORE,
        secondaryMuscles = parseMuscles(secondaryMuscles),
        repMin = repMin,
        repMax = repMax,
        incrementKg = incrementKg,
        startKg = startKg,
        addedLoad = addedLoad,
    )
}

private fun SessionEntity.toDomain() = Session(id = id, startedAt = startedAt, finishedAt = finishedAt)

private fun SetEntity.toDomain() = SetEntry(
    id = id,
    sessionId = sessionId,
    exerciseId = exerciseId,
    setNumber = setNumber,
    weightKg = weightKg,
    reps = reps,
    completedAt = completedAt,
)
