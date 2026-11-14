package com.example.gymapp.domain

import java.util.Locale
import kotlin.math.roundToInt

data class ExerciseTrend(
    val name: String,
    val sessions: Int,
    val bestSet: String,
    val e1rmChangePct: Double?,
)

data class WeeklyDigest(
    val sessionCount: Int,
    val previousSessionCount: Int,
    val setCount: Int,
    val volumeKg: Double,
    val previousVolumeKg: Double,
    val landmarks: List<MuscleVolumeLandmark>,
    val trends: List<ExerciseTrend>,
    val stalled: List<String>,
    val mealDaysLogged: Int,
    val avgKcal: Double?,
    val avgProteinG: Double?,
    val targets: Macros,
    val goal: NutritionGoal,
    val profile: BodyProfile,
) {
    val hasTraining: Boolean get() = sessionCount > 0

    /** Plain-text summary sent to the model; numbers only, no personal identifiers. */
    fun toPromptText(): String = buildString {
        appendLine("HỒ SƠ: ${profile.sex.label}, ${profile.ageYears} tuổi, ${fmt(profile.weightKg)} kg, mỡ ${fmt(profile.bodyFatPct)}%, cao ${profile.heightCm} cm. Mục tiêu: ${goal.label}.")
        appendLine("TẬP 7 NGÀY QUA: $sessionCount buổi (7 ngày trước đó: $previousSessionCount), $setCount hiệp, khối lượng ${volumeKg.roundToInt()} kg (trước đó ${previousVolumeKg.roundToInt()} kg).")
        appendLine("HIỆP THEO NHÓM CƠ (thực tế / MEV–MRV):")
        landmarks.forEach { appendLine("- ${it.title}: ${it.setsCompleted} / ${it.mevSets}–${it.mrvSets}") }
        if (trends.isNotEmpty()) {
            appendLine("BÀI TẬP TUẦN NÀY (set tốt nhất, thay đổi e1RM so với 4 tuần trước):")
            trends.forEach { t ->
                val change = t.e1rmChangePct?.let { " (${if (it >= 0) "+" else ""}${fmt(it)}%)" } ?: " (chưa có dữ liệu so sánh)"
                appendLine("- ${t.name}: ${t.sessions} buổi, ${t.bestSet}$change")
            }
        }
        if (stalled.isNotEmpty()) appendLine("CHỮNG LẠI ≥3 buổi không tăng e1RM: ${stalled.joinToString(", ")}")
        if (mealDaysLogged == 0) {
            appendLine("DINH DƯỠNG: không ghi bữa ăn nào trong 7 ngày.")
        } else {
            appendLine(
                "DINH DƯỠNG: ghi $mealDaysLogged/7 ngày, trung bình ${avgKcal?.roundToInt()} kcal và ${avgProteinG?.roundToInt()} g đạm mỗi ngày có ghi " +
                    "(mục tiêu ${targets.kcal.roundToInt()} kcal, ${targets.proteinG.roundToInt()} g đạm). Ngày không ghi có thể vẫn ăn.",
            )
        }
    }

    private fun fmt(value: Double) = String.format(Locale.US, "%.1f", value).removeSuffix(".0")

    companion object {
        private const val DAY_MS = 24L * 60 * 60 * 1000

        fun build(
            exercises: List<Exercise>,
            sessions: List<Session>,
            sets: List<SetEntry>,
            meals: List<Meal>,
            profile: BodyProfile,
            goal: NutritionGoal,
            targets: Macros,
            nowMs: Long,
            dayKey: (Long) -> Long,
        ): WeeklyDigest {
            val weekStart = nowMs - 7 * DAY_MS
            val prevStart = nowMs - 14 * DAY_MS
            val baselineStart = weekStart - 28 * DAY_MS
            val trained = sets.map { it.sessionId }.toSet()
            val inWeek = sessions.filter { it.startedAt in weekStart..nowMs && it.id in trained }
            val inPrev = sessions.filter { it.startedAt in prevStart until weekStart && it.id in trained }
            val weekIds = inWeek.map { it.id }.toSet()
            val prevIds = inPrev.map { it.id }.toSet()
            val weekSets = sets.filter { it.sessionId in weekIds }

            val byId = exercises.associateBy { it.id }
            val trends = weekSets.map { it.exerciseId }.distinct().mapNotNull { exId ->
                val ex = byId[exId] ?: return@mapNotNull null
                val points = pointsForExercise(sets, sessions, exId)
                val weekPoints = points.filter { it.startedAt in weekStart..nowMs }
                val best = weekPoints.maxByOrNull { it.bestE1rm ?: 0.0 } ?: return@mapNotNull null
                val baseline = points.filter { it.startedAt in baselineStart until weekStart }
                    .mapNotNull { it.bestE1rm }.maxOrNull()
                val change = if (best.bestE1rm != null && baseline != null && baseline > 0.0) {
                    (best.bestE1rm - baseline) / baseline * 100.0
                } else {
                    null
                }
                val bestSet = if (best.e1rmWeightKg != null && best.e1rmReps != null) {
                    "${formatLoad(best.e1rmWeightKg, ex.addedLoad)} × ${best.e1rmReps}"
                } else {
                    "${best.bestReps} rep"
                }
                ExerciseTrend(ex.name, weekPoints.size, bestSet, change)
            }.sortedByDescending { it.sessions }

            val stalled = exercises.filter { ex ->
                val recent = pointsForExercise(sets, sessions, ex.id)
                    .filter { it.startedAt in (nowMs - 28 * DAY_MS)..nowMs }
                    .mapNotNull { it.bestE1rm }
                recent.size >= 3 && recent.last() <= recent.first() * 1.005
            }.map { it.name }

            val weekMeals = meals.filter { it.eatenAt in weekStart..nowMs }
            val byDay = weekMeals.groupBy { dayKey(it.eatenAt) }
            val dailyTotals = byDay.values.map { dayMeals -> dayMeals.fold(Macros()) { acc, m -> acc + m.macros } }

            return WeeklyDigest(
                sessionCount = inWeek.size,
                previousSessionCount = inPrev.size,
                setCount = weekSets.size,
                volumeKg = weekSets.sumOf { it.weightKg * it.reps },
                previousVolumeKg = sets.filter { it.sessionId in prevIds }.sumOf { it.weightKg * it.reps },
                landmarks = IronLogCatalog.computeLandmarks(exercises, sessions, sets, weekStart, nowMs + 1),
                trends = trends.take(12),
                stalled = stalled,
                mealDaysLogged = byDay.size,
                avgKcal = dailyTotals.takeIf { it.isNotEmpty() }?.map { it.kcal }?.average(),
                avgProteinG = dailyTotals.takeIf { it.isNotEmpty() }?.map { it.proteinG }?.average(),
                targets = targets,
                goal = goal,
                profile = profile,
            )
        }
    }
}

data class CoachReport(
    val headline: String,
    val wins: List<String>,
    val issues: List<String>,
    val actions: List<String>,
    val deloadSuggested: Boolean,
    val generatedAt: Long,
)
