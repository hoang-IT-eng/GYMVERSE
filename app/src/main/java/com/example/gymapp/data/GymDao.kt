package com.example.gymapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GymDao {
    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun countExercises(): Int

    @Insert
    suspend fun insertExercises(exercises: List<ExerciseEntity>)

    @Insert
    suspend fun insertExercise(exercise: ExerciseEntity): Long

    @Query("SELECT * FROM exercises ORDER BY name")
    suspend fun getAllExercises(): List<ExerciseEntity>

    @Query("SELECT * FROM exercises ORDER BY name")
    fun observeExercises(): Flow<List<ExerciseEntity>>

    @Insert
    suspend fun insertSession(session: SessionEntity): Long

    @Query("UPDATE sessions SET finishedAt = :finishedAt WHERE id = :id")
    suspend fun finishSession(id: Long, finishedAt: Long)

    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun deleteSession(id: Long)

    @Query("SELECT * FROM sessions")
    fun observeSessions(): Flow<List<SessionEntity>>

    @Insert
    suspend fun insertSessionExercise(link: SessionExerciseEntity)

    @Query("SELECT COALESCE(MAX(position), -1) FROM session_exercises WHERE sessionId = :sessionId")
    suspend fun maxPosition(sessionId: Long): Int

    @Query("SELECT COUNT(*) FROM session_exercises WHERE sessionId = :sessionId AND exerciseId = :exerciseId")
    suspend fun countLink(sessionId: Long, exerciseId: Long): Int

    @Query("DELETE FROM session_exercises WHERE sessionId = :sessionId AND exerciseId = :exerciseId")
    suspend fun deleteLink(sessionId: Long, exerciseId: Long)

    @Query("SELECT * FROM session_exercises")
    fun observeLinks(): Flow<List<SessionExerciseEntity>>

    @Insert
    suspend fun insertSet(set: SetEntity): Long

    @Query("SELECT COUNT(*) FROM sets WHERE sessionId = :sessionId AND exerciseId = :exerciseId")
    suspend fun countSets(sessionId: Long, exerciseId: Long): Int

    @Query("UPDATE sets SET weightKg = :weightKg, reps = :reps WHERE id = :id")
    suspend fun updateSet(id: Long, weightKg: Double, reps: Int)

    @Query("DELETE FROM sets WHERE id = :id")
    suspend fun deleteSet(id: Long)

    @Query("DELETE FROM sets WHERE sessionId = :sessionId AND exerciseId = :exerciseId")
    suspend fun deleteSetsForExercise(sessionId: Long, exerciseId: Long)

    @Query("SELECT * FROM sets")
    fun observeSets(): Flow<List<SetEntity>>

    @Insert
    suspend fun insertCustomRoutine(routine: CustomRoutineEntity): Long

    @Query("UPDATE custom_routines SET name = :name WHERE id = :id")
    suspend fun renameCustomRoutine(id: Long, name: String)

    @Query("DELETE FROM custom_routines WHERE id = :id")
    suspend fun deleteCustomRoutine(id: Long)

    @Query("SELECT * FROM custom_routines")
    fun observeCustomRoutines(): Flow<List<CustomRoutineEntity>>

    @Insert
    suspend fun insertCustomDay(day: CustomRoutineDayEntity): Long

    @Query("DELETE FROM custom_routine_days WHERE routineId = :routineId")
    suspend fun deleteDaysForRoutine(routineId: Long)

    @Query("SELECT * FROM custom_routine_days")
    fun observeCustomDays(): Flow<List<CustomRoutineDayEntity>>

    @Insert
    suspend fun insertCustomDayExercises(links: List<CustomRoutineExerciseEntity>)

    @Query("SELECT * FROM custom_routine_exercises")
    fun observeCustomDayExercises(): Flow<List<CustomRoutineExerciseEntity>>
}
