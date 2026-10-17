package com.example.gymapp.domain

data class CustomRoutineDay(
    val id: Long,
    val title: String,
    val isRest: Boolean,
    val exerciseIds: List<Long>,
)

data class CustomRoutine(
    val id: Long,
    val name: String,
    val days: List<CustomRoutineDay>,
) {
    fun planId(): String = "custom:$id"
}

fun CustomRoutine.toPlan(exercisesById: Map<Long, Exercise>): RoutinePlan {
    val specs = days.mapIndexed { index, day ->
        val names = day.exerciseIds.mapNotNull { exercisesById[it]?.name }
        RoutineDaySpec(
            dayNumber = index + 1,
            title = day.title.ifBlank { if (day.isRest) "Ngày nghỉ" else "Ngày ${index + 1}" },
            subtitle = if (day.isRest) "Nghỉ" else "${names.size} bài",
            targetRpe = "RIR 1 - 2",
            exerciseNames = names,
            exerciseIds = day.exerciseIds,
            isRest = day.isRest,
        )
    }
    val trainingDays = specs.count { !it.isRest }
    return RoutinePlan(
        id = planId(),
        name = name,
        subtitle = "Giáo án tự soạn",
        daysPerWeek = "$trainingDays ngày/tuần",
        durationText = "tự chọn",
        frequencyText = "của bạn",
        badges = listOf("Của tôi"),
        filterTag = "custom",
        days = specs,
    )
}
