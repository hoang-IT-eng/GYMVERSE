package com.example.gymapp.domain

/**
 * Sessions do not record which routine day they came from, so the last day
 * is inferred from exercise overlap: newest session first, the day sharing
 * the largest share of its exercises wins, ties go to the earlier day.
 * Sessions that match no day (free workouts) are skipped.
 */
object RoutineRotation {
    fun nextDayIndex(
        dayExerciseIds: List<Set<Long>>,
        recentSessionsNewestFirst: List<Set<Long>>,
    ): Int {
        if (dayExerciseIds.isEmpty()) return 0
        for (trained in recentSessionsNewestFirst) {
            if (trained.isEmpty()) continue
            var bestIndex = -1
            var bestScore = 0.0
            dayExerciseIds.forEachIndexed { index, ids ->
                if (ids.isEmpty()) return@forEachIndexed
                val score = ids.count { it in trained }.toDouble() / ids.size
                if (score > bestScore) {
                    bestScore = score
                    bestIndex = index
                }
            }
            if (bestIndex >= 0) return (bestIndex + 1) % dayExerciseIds.size
        }
        return 0
    }
}
