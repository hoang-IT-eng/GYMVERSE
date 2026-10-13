package com.example.gymapp.data

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
        return combine(
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
    }

    suspend fun startSession(now: Long = System.currentTimeMillis()): Long {
        return dao.insertSession(SessionEntity(startedAt = now))
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
