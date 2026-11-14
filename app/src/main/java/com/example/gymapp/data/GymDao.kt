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

    @Query("SELECT * FROM sessions ORDER BY startedAt")
    suspend fun getAllSessions(): List<SessionEntity>

    @Query("SELECT * FROM session_exercises")
    suspend fun getAllSessionExercises(): List<SessionExerciseEntity>

    @Query("SELECT * FROM sets ORDER BY completedAt")
    suspend fun getAllSets(): List<SetEntity>

    @Query("SELECT * FROM custom_routines")
    suspend fun getAllCustomRoutines(): List<CustomRoutineEntity>

    @Query("SELECT * FROM custom_routine_days")
    suspend fun getAllCustomDays(): List<CustomRoutineDayEntity>

    @Query("SELECT * FROM custom_routine_exercises")
    suspend fun getAllCustomDayExercises(): List<CustomRoutineExerciseEntity>

    @Insert
    suspend fun insertSessions(sessions: List<SessionEntity>)

    @Insert
    suspend fun insertSessionExercises(links: List<SessionExerciseEntity>)

    @Insert
    suspend fun insertSets(sets: List<SetEntity>)

    @Insert
    suspend fun insertCustomRoutines(routines: List<CustomRoutineEntity>)

    @Insert
    suspend fun insertCustomDays(days: List<CustomRoutineDayEntity>)

    @Query("DELETE FROM sets")
    suspend fun clearSets()

    @Query("DELETE FROM session_exercises")
    suspend fun clearSessionExercises()

    @Query("DELETE FROM sessions")
    suspend fun clearSessions()

    @Query("DELETE FROM custom_routine_exercises")
    suspend fun clearCustomDayExercises()

    @Query("DELETE FROM custom_routine_days")
    suspend fun clearCustomDays()

    @Query("DELETE FROM custom_routines")
    suspend fun clearCustomRoutines()

    @Query("DELETE FROM exercises")
    suspend fun clearExercises()

    @Insert
    suspend fun insertMeal(meal: MealEntity): Long

    @Insert
    suspend fun insertMeals(meals: List<MealEntity>)

    @Insert
    suspend fun insertMealItems(items: List<MealItemEntity>)

    @Query("DELETE FROM meals WHERE id = :id")
    suspend fun deleteMeal(id: Long)

    @Query("UPDATE meals SET title = :title WHERE id = :id")
    suspend fun updateMealTitle(id: Long, title: String)

    @Query("DELETE FROM meal_items WHERE mealId = :mealId")
    suspend fun deleteMealItems(mealId: Long)

    @Query("SELECT * FROM meals WHERE eatenAt >= :fromMs AND eatenAt < :toMs ORDER BY eatenAt DESC")
    suspend fun getMealsBetween(fromMs: Long, toMs: Long): List<MealEntity>

    @Query("SELECT * FROM meal_items WHERE mealId IN (SELECT id FROM meals WHERE eatenAt >= :fromMs AND eatenAt < :toMs)")
    suspend fun getMealItemsBetween(fromMs: Long, toMs: Long): List<MealItemEntity>

    @Query("SELECT * FROM meals WHERE eatenAt >= :fromMs AND eatenAt < :toMs ORDER BY eatenAt DESC")
    fun observeMealsBetween(fromMs: Long, toMs: Long): Flow<List<MealEntity>>

    @Query("SELECT * FROM meal_items WHERE mealId IN (SELECT id FROM meals WHERE eatenAt >= :fromMs AND eatenAt < :toMs)")
    fun observeMealItemsBetween(fromMs: Long, toMs: Long): Flow<List<MealItemEntity>>

    @Query("SELECT * FROM meals ORDER BY eatenAt")
    suspend fun getAllMeals(): List<MealEntity>

    @Query("SELECT * FROM meal_items")
    suspend fun getAllMealItems(): List<MealItemEntity>

    @Query("DELETE FROM meal_items")
    suspend fun clearMealItems()

    @Query("DELETE FROM meals")
    suspend fun clearMeals()
}
