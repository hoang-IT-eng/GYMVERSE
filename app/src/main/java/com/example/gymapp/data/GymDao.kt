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
}
