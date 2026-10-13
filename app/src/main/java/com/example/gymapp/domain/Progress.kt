package com.example.gymapp.domain

data class SessionPerformance(
    val sessionId: Long,
    val startedAt: Long,
    val sets: List<SetPerformance>,
)

data class ProgressPoint(
    val sessionId: Long,
    val startedAt: Long,
    val bestE1rm: Double?,
    val e1rmWeightKg: Double?,
    val e1rmReps: Int?,
    val bestReps: Int,
    val totalReps: Int,
    val volumeKg: Double,
)

fun progressPoints(sessions: List<SessionPerformance>): List<ProgressPoint> {
    return sessions.map { session ->
        val source = epleySource(session.sets)
        ProgressPoint(
            sessionId = session.sessionId,
            startedAt = session.startedAt,
            bestE1rm = source?.estimate,
            e1rmWeightKg = source?.weightKg,
            e1rmReps = source?.reps,
            bestReps = session.sets.maxOfOrNull { it.reps } ?: 0,
            totalReps = session.sets.sumOf { it.reps },
            volumeKg = session.sets.sumOf { it.weightKg * it.reps },
        )
    }
}

fun pointsForExercise(
    sets: List<SetEntry>,
    sessions: List<Session>,
    exerciseId: Long,
): List<ProgressPoint> {
    val startedAt = sessions.associate { it.id to it.startedAt }
    val grouped = sets.filter { it.exerciseId == exerciseId }.groupBy { it.sessionId }
    val performances = grouped.map { (sessionId, sessionSets) ->
        SessionPerformance(
            sessionId = sessionId,
            startedAt = startedAt[sessionId] ?: sessionSets.minOf { it.completedAt },
            sets = sessionSets.sortedBy { it.completedAt }.map { SetPerformance(it.weightKg, it.reps) },
        )
    }.sortedBy { it.startedAt }
    return progressPoints(performances)
}
