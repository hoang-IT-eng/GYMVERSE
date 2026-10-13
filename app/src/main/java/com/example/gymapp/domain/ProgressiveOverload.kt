package com.example.gymapp.domain

data class OverloadSuggestion(
    val weightKg: Double,
    val targetReps: Int,
    val reason: String,
    val hasHistory: Boolean,
)

/**
 * Double progression on the heaviest sets of the previous session.
 * Every one of those sets at the top of the rep range adds weight and
 * resets reps to the bottom. Missing the floor on all of them drops weight.
 * Otherwise the next target is one rep above the weakest counted set.
 * A set far below the best of the day is ignored so one missed rep-out
 * does not drag the suggestion down.
 */
object ProgressiveOverload {
    fun suggest(
        lastSessionSets: List<SetPerformance>,
        repMin: Int,
        repMax: Int,
        incrementKg: Double,
        startKg: Double,
    ): OverloadSuggestion {
        val sets = lastSessionSets.filter { it.reps > 0 && it.weightKg >= 0.0 }
        if (sets.isEmpty()) {
            return OverloadSuggestion(
                weightKg = normalizeKg(startKg),
                targetReps = repMin,
                reason = "Buổi đầu — chọn tạ bạn làm được $repMin–$repMax rep",
                hasHistory = false,
            )
        }

        val topWeight = normalizeKg(sets.maxOf { it.weightKg })
        val topSets = sets.filter { normalizeKg(it.weightKg) == topWeight }
        val bestReps = topSets.maxOf { it.reps }
        val counted = topSets.filter { it.reps >= bestReps - 3 }.ifEmpty { topSets }
        val minReps = counted.minOf { it.reps }
        val maxReps = counted.maxOf { it.reps }
        val step = incrementKg.coerceAtLeast(0.0)

        if (minReps >= repMax) {
            if (step > 0.0) {
                return OverloadSuggestion(
                    weightKg = normalizeKg(topWeight + step),
                    targetReps = repMin,
                    reason = "Mọi set đạt $repMax rep → tăng ${formatKg(step)} kg",
                    hasHistory = true,
                )
            }
            return OverloadSuggestion(
                weightKg = topWeight,
                targetReps = repMax,
                reason = "Đã chạm trần $repMax rep",
                hasHistory = true,
            )
        }

        if (maxReps < repMin) {
            val reduced = normalizeKg(topWeight - step)
            if (step > 0.0 && reduced >= 0.0 && reduced < topWeight) {
                return OverloadSuggestion(
                    weightKg = reduced,
                    targetReps = repMin,
                    reason = "Chưa đạt $repMin rep → giảm ${formatKg(step)} kg",
                    hasHistory = true,
                )
            }
            return OverloadSuggestion(
                weightKg = topWeight,
                targetReps = repMin,
                reason = "Chưa đạt $repMin rep — giữ tạ, ưu tiên kỹ thuật",
                hasHistory = true,
            )
        }

        return OverloadSuggestion(
            weightKg = topWeight,
            targetReps = minReps + 1,
            reason = "Giữ tạ, nhắm ${minReps + 1} rep",
            hasHistory = true,
        )
    }
}
