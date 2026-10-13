package com.example.gymapp.domain

/**
 * Epley estimate. Reliable enough for 1–12 reps; higher-rep sets are omitted
 * because the formula inflates them.
 */
fun epley1Rm(weightKg: Double, reps: Int): Double? {
    if (weightKg <= 0.0 || reps <= 0) return null
    if (reps == 1) return weightKg
    if (reps > 12) return null
    return weightKg * (1.0 + reps / 30.0)
}

fun bestE1rm(sets: List<SetPerformance>): Double? {
    return sets.mapNotNull { epley1Rm(it.weightKg, it.reps) }.maxOrNull()
}

data class EpleySource(
    val weightKg: Double,
    val reps: Int,
    val estimate: Double,
)

fun epleySource(sets: List<SetPerformance>): EpleySource? {
    return sets.mapNotNull { set ->
        epley1Rm(set.weightKg, set.reps)?.let { estimate ->
            EpleySource(set.weightKg, set.reps, estimate)
        }
    }.maxByOrNull { it.estimate }
}

/** Earliest set that still holds the best estimated 1RM. Needs a comparison set. */
fun recordHolderId(sets: List<SetEntry>): Long? {
    val scored = sets.mapNotNull { set ->
        epley1Rm(set.weightKg, set.reps)?.let { estimate ->
            Triple(set.id, set.completedAt, estimate)
        }
    }
    if (scored.size < 2) return null
    val best = scored.maxOf { it.third }
    return scored.filter { it.third >= best - 0.05 }.minByOrNull { it.second }?.first
}
