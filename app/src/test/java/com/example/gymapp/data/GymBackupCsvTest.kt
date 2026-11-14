package com.example.gymapp.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneOffset

class GymBackupCsvTest {
    private val data = BackupData(
        exercises = listOf(
            ExerciseEntity(
                id = 1,
                name = "Bench Press, Barbell",
                muscle = "CHEST",
                secondaryMuscles = "",
                repMin = 6,
                repMax = 10,
                incrementKg = 2.5,
                startKg = 20.0,
                addedLoad = false,
            ),
        ),
        sessions = listOf(SessionEntity(id = 7, startedAt = 0L, finishedAt = 3_600_000L)),
        sessionExercises = listOf(SessionExerciseEntity(id = 1, sessionId = 7, exerciseId = 1, position = 0)),
        sets = listOf(
            SetEntity(id = 2, sessionId = 7, exerciseId = 1, setNumber = 2, weightKg = 62.5, reps = 8, completedAt = 120_000L),
            SetEntity(id = 1, sessionId = 7, exerciseId = 1, setNumber = 1, weightKg = 60.0, reps = 15, completedAt = 60_000L),
        ),
        customRoutines = emptyList(),
        customRoutineDays = emptyList(),
        customRoutineExercises = emptyList(),
    )

    @Test
    fun writesOneQuotedRowPerSetInTimeOrder() {
        val lines = GymBackup.toCsv(data, ZoneOffset.UTC).removePrefix("\uFEFF").lines()
        assertEquals("ngay,buoi_tap,bai_tap,nhom_co,hiep,ta_kg,reps,khoi_luong_kg,e1rm_kg", lines[0])
        assertEquals("1970-01-01 00:01,7,\"Bench Press, Barbell\",Ngực,1,60,15,900,", lines[1])
        assertEquals("1970-01-01 00:02,7,\"Bench Press, Barbell\",Ngực,2,62.5,8,500,79.2", lines[2])
    }

    @Test
    fun startsWithByteOrderMarkForExcel() {
        assertEquals('\uFEFF', GymBackup.toCsv(data, ZoneOffset.UTC).first())
    }
}
