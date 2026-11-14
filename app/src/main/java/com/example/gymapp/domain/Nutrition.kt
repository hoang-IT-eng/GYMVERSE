package com.example.gymapp.domain

import kotlin.math.roundToInt

data class FoodItem(
    val name: String,
    val grams: Double,
    val kcalPer100g: Double,
    val proteinPer100g: Double,
    val carbsPer100g: Double,
    val fatPer100g: Double,
    /** Values came from [VietFoodDb] rather than the AI's guess; not persisted. */
    val fromDatabase: Boolean = false,
) {
    val macros: Macros
        get() {
            val factor = grams.coerceAtLeast(0.0) / 100.0
            return Macros(
                kcal = kcalPer100g * factor,
                proteinG = proteinPer100g * factor,
                carbsG = carbsPer100g * factor,
                fatG = fatPer100g * factor,
            )
        }
}

data class Macros(
    val kcal: Double = 0.0,
    val proteinG: Double = 0.0,
    val carbsG: Double = 0.0,
    val fatG: Double = 0.0,
) {
    operator fun plus(other: Macros) = Macros(
        kcal = kcal + other.kcal,
        proteinG = proteinG + other.proteinG,
        carbsG = carbsG + other.carbsG,
        fatG = fatG + other.fatG,
    )
}

fun List<FoodItem>.totalMacros(): Macros = fold(Macros()) { acc, item -> acc + item.macros }

data class Meal(
    val id: Long,
    val eatenAt: Long,
    val title: String,
    val source: MealSource,
    val confidence: AnalysisConfidence?,
    val items: List<FoodItem>,
    /** File name inside the app's meal photo folder. */
    val photoName: String? = null,
) {
    val macros: Macros get() = items.totalMacros()
}

enum class MealSource(val key: String) {
    PHOTO("photo"),
    TEXT("text"),
    MANUAL("manual"),
    ;

    companion object {
        fun fromKey(key: String) = entries.firstOrNull { it.key == key } ?: TEXT
    }
}

enum class AnalysisConfidence(val key: String, val label: String) {
    LOW("low", "Độ tin cậy thấp"),
    MEDIUM("medium", "Độ tin cậy vừa"),
    HIGH("high", "Độ tin cậy cao"),
    ;

    companion object {
        fun fromKey(key: String?) = entries.firstOrNull { it.key == key }
    }
}

data class MealAnalysis(
    val isFood: Boolean,
    val dish: String,
    val confidence: AnalysisConfidence?,
    val items: List<FoodItem>,
)

enum class NutritionGoal(val key: String, val label: String, val kcalDelta: Int, val proteinPerKg: Double) {
    CUT("cut", "Giảm mỡ", -400, 2.2),
    MAINTAIN("maintain", "Giữ cân", 0, 1.8),
    LEAN_BULK("lean_bulk", "Lean bulk", 250, 2.0),
    ;

    companion object {
        fun fromKey(key: String?) = entries.firstOrNull { it.key == key } ?: LEAN_BULK
    }
}

enum class Sex(val key: String, val label: String) {
    MALE("male", "Nam"),
    FEMALE("female", "Nữ"),
    ;

    companion object {
        fun fromKey(key: String?) = entries.firstOrNull { it.key == key } ?: MALE
    }
}

enum class ActivityLevel(val key: String, val label: String, val detail: String, val factor: Double) {
    SEDENTARY("sedentary", "Ít vận động", "Ngồi nhiều, không tập", 1.2),
    LIGHT("light", "Nhẹ", "Tập 1–3 buổi/tuần", 1.375),
    MODERATE("moderate", "Vừa", "Tập 3–5 buổi/tuần", 1.55),
    HIGH("high", "Cao", "Tập 6–7 buổi/tuần", 1.725),
    ATHLETE("athlete", "Rất cao", "Tập 2 buổi/ngày hoặc lao động nặng", 1.9),
    ;

    companion object {
        fun fromKey(key: String?) = entries.firstOrNull { it.key == key } ?: MODERATE
    }
}

data class BodyProfile(
    val weightKg: Double,
    val bodyFatPct: Double,
    val heightCm: Int,
    val ageYears: Int,
    val sex: Sex,
) {
    val leanMassKg: Double get() = weightKg * (1.0 - bodyFatPct.coerceIn(3.0, 60.0) / 100.0)
}

data class EnergyBreakdown(
    val bmrKatch: Double,
    val bmrMifflin: Double,
    val activity: ActivityLevel,
    val tdee: Double,
    val goal: NutritionGoal,
    val targetKcal: Double,
)

fun katchMcArdleBmr(leanMassKg: Double): Double = 370.0 + 21.6 * leanMassKg

fun mifflinStJeorBmr(weightKg: Double, heightCm: Int, ageYears: Int, sex: Sex): Double =
    10.0 * weightKg + 6.25 * heightCm - 5.0 * ageYears + if (sex == Sex.MALE) 5.0 else -161.0

/**
 * Katch-McArdle drives the target because it uses lean mass, which suits
 * lifters better than height/age formulas; Mifflin-St Jeor is shown for
 * comparison. Calories never drop below 1200.
 */
fun energyBreakdown(profile: BodyProfile, activity: ActivityLevel, goal: NutritionGoal): EnergyBreakdown {
    val bmr = katchMcArdleBmr(profile.leanMassKg)
    val tdee = bmr * activity.factor
    return EnergyBreakdown(
        bmrKatch = bmr,
        bmrMifflin = mifflinStJeorBmr(profile.weightKg, profile.heightCm, profile.ageYears, profile.sex),
        activity = activity,
        tdee = tdee,
        goal = goal,
        targetKcal = (tdee + goal.kcalDelta).coerceAtLeast(1200.0),
    )
}

/** Protein scales with body weight; fat takes 25% of calories and carbs fill the rest. */
fun macrosFor(kcal: Double, weightKg: Double, goal: NutritionGoal): Macros {
    val protein = weightKg * goal.proteinPerKg
    val fat = kcal * 0.25 / 9.0
    val carbs = ((kcal - protein * 4.0 - fat * 9.0) / 4.0).coerceAtLeast(0.0)
    return Macros(kcal = kcal, proteinG = protein, carbsG = carbs, fatG = fat)
}

fun nutritionTargets(
    weightKg: Double,
    bodyFatPct: Double,
    goal: NutritionGoal,
    activityFactor: Double = 1.55,
): Macros {
    val leanMassKg = weightKg * (1.0 - bodyFatPct.coerceIn(3.0, 60.0) / 100.0)
    val kcal = (katchMcArdleBmr(leanMassKg) * activityFactor + goal.kcalDelta).coerceAtLeast(1200.0)
    return macrosFor(kcal, weightKg, goal)
}

/** Photo estimates are typically off by a quarter or more, so show a band rather than one number. */
fun kcalRange(kcal: Double, spread: Double = 0.25): IntRange {
    val low = (kcal * (1.0 - spread) / 10.0).roundToInt() * 10
    val high = (kcal * (1.0 + spread) / 10.0).roundToInt() * 10
    return low..high
}
