package com.example.gymapp.domain

enum class MuscleGroup(val label: String) {
    CHEST("Ngực"),
    BACK("Lưng"),
    SHOULDERS("Vai"),
    BICEPS("Tay trước"),
    TRICEPS("Tay sau"),
    QUADS("Đùi trước"),
    HAMSTRINGS("Đùi sau"),
    GLUTES("Mông"),
    CALVES("Bắp chân"),
    CORE("Bụng"),
}

data class Exercise(
    val id: Long,
    val name: String,
    val muscle: MuscleGroup,
    val secondaryMuscles: List<MuscleGroup>,
    val repMin: Int,
    val repMax: Int,
    val incrementKg: Double,
    val startKg: Double,
    val addedLoad: Boolean,
)

data class Session(
    val id: Long,
    val startedAt: Long,
    val finishedAt: Long?,
)

data class SessionLink(
    val sessionId: Long,
    val exerciseId: Long,
    val position: Int,
)

data class SetEntry(
    val id: Long,
    val sessionId: Long,
    val exerciseId: Long,
    val setNumber: Int,
    val weightKg: Double,
    val reps: Int,
    val completedAt: Long,
)

data class SetPerformance(
    val weightKg: Double,
    val reps: Int,
)

fun Exercise.muscleLine(): String {
    if (secondaryMuscles.isEmpty()) return muscle.label
    val extra = secondaryMuscles.joinToString(", ") { it.label }
    return "${muscle.label} · $extra"
}

fun parseMuscles(value: String): List<MuscleGroup> {
    return value.split(',')
        .mapNotNull { token ->
            val name = token.trim()
            if (name.isEmpty()) null else MuscleGroup.entries.firstOrNull { it.name == name }
        }
}
