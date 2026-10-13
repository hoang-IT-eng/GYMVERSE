package com.example.gymapp.data

import com.example.gymapp.domain.MuscleGroup

object ExerciseSeed {
    fun entities(): List<ExerciseEntity> = listOf(
        ex("Bench Press", MuscleGroup.CHEST, 6, 10, 2.5, 20.0, listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS)),
        ex("Incline Dumbbell Press", MuscleGroup.CHEST, 8, 12, 2.0, 10.0, listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS)),
        ex("Dumbbell Fly", MuscleGroup.CHEST, 10, 15, 2.0, 8.0),
        ex("Deadlift", MuscleGroup.BACK, 3, 6, 5.0, 40.0, listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES)),
        ex("Barbell Row", MuscleGroup.BACK, 6, 10, 2.5, 20.0, listOf(MuscleGroup.BICEPS)),
        ex("Lat Pulldown", MuscleGroup.BACK, 8, 12, 5.0, 30.0, listOf(MuscleGroup.BICEPS)),
        ex("Seated Cable Row", MuscleGroup.BACK, 8, 12, 5.0, 25.0, listOf(MuscleGroup.BICEPS)),
        ex("Pull-up", MuscleGroup.BACK, 5, 10, 2.5, 0.0, listOf(MuscleGroup.BICEPS), added = true),
        ex("Overhead Press", MuscleGroup.SHOULDERS, 6, 10, 2.5, 20.0, listOf(MuscleGroup.TRICEPS)),
        ex("Lateral Raise", MuscleGroup.SHOULDERS, 12, 20, 2.0, 4.0),
        ex("Face Pull", MuscleGroup.SHOULDERS, 12, 20, 2.5, 10.0),
        ex("Barbell Curl", MuscleGroup.BICEPS, 8, 12, 2.5, 20.0),
        ex("Hammer Curl", MuscleGroup.BICEPS, 8, 12, 2.0, 8.0),
        ex("Triceps Pushdown", MuscleGroup.TRICEPS, 10, 15, 5.0, 15.0),
        ex("Overhead Triceps Extension", MuscleGroup.TRICEPS, 8, 12, 2.0, 10.0),
        ex("Dip", MuscleGroup.TRICEPS, 6, 12, 2.5, 0.0, listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS), added = true),
        ex("Squat", MuscleGroup.QUADS, 5, 8, 5.0, 20.0, listOf(MuscleGroup.GLUTES)),
        ex("Leg Press", MuscleGroup.QUADS, 8, 12, 10.0, 40.0, listOf(MuscleGroup.GLUTES)),
        ex("Walking Lunge", MuscleGroup.QUADS, 8, 12, 2.0, 10.0, listOf(MuscleGroup.GLUTES)),
        ex("Romanian Deadlift", MuscleGroup.HAMSTRINGS, 6, 10, 5.0, 40.0, listOf(MuscleGroup.GLUTES, MuscleGroup.BACK)),
        ex("Leg Curl", MuscleGroup.HAMSTRINGS, 8, 12, 5.0, 20.0),
        ex("Hip Thrust", MuscleGroup.GLUTES, 8, 12, 5.0, 40.0, listOf(MuscleGroup.HAMSTRINGS)),
        ex("Calf Raise", MuscleGroup.CALVES, 10, 15, 5.0, 20.0),
        ex("Cable Crunch", MuscleGroup.CORE, 10, 15, 5.0, 20.0),
        ex("Hanging Leg Raise", MuscleGroup.CORE, 8, 15, 0.0, 0.0, added = true),
    )

    private fun ex(
        name: String,
        muscle: MuscleGroup,
        repMin: Int,
        repMax: Int,
        incrementKg: Double,
        startKg: Double,
        secondary: List<MuscleGroup> = emptyList(),
        added: Boolean = false,
    ) = ExerciseEntity(
        name = name,
        muscle = muscle.name,
        secondaryMuscles = secondary.joinToString(",") { it.name },
        repMin = repMin,
        repMax = repMax,
        incrementKg = incrementKg,
        startKg = startKg,
        addedLoad = added,
    )
}
