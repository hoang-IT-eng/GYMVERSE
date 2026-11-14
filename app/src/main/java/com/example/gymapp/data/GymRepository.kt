package com.example.gymapp.data

import androidx.room.withTransaction
import com.example.gymapp.domain.AnalysisConfidence
import com.example.gymapp.domain.CustomRoutine
import com.example.gymapp.domain.CustomRoutineDay
import com.example.gymapp.domain.Exercise
import com.example.gymapp.domain.FoodItem
import com.example.gymapp.domain.Meal
import com.example.gymapp.domain.MealSource
import com.example.gymapp.domain.MuscleGroup
import com.example.gymapp.domain.Session
import com.example.gymapp.domain.SessionLink
import com.example.gymapp.domain.SetEntry
import com.example.gymapp.domain.normalizeKg
import com.example.gymapp.domain.parseMuscles
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class GymSnapshot(
    val exercises: List<Exercise>,
    val sessions: List<Session>,
    val links: List<SessionLink>,
    val sets: List<SetEntry>,
    val customRoutines: List<CustomRoutine> = emptyList(),
)

data class RoutineDayDraft(
    val title: String,
    val isRest: Boolean,
    val exerciseIds: List<Long>,
)

class GymRepository(private val db: GymDatabase) {
    private val dao: GymDao = db.dao()
    private val seedLock = Mutex()
    private var seeded = false

    suspend fun ensureSeeded() {
        if (seeded) return
        seedLock.withLock {
            if (seeded) return@withLock
            if (dao.countExercises() == 0) {
                dao.insertExercises(ExerciseSeed.entities())
            }
            seeded = true
        }
    }

    fun observeGym(): Flow<GymSnapshot> {
        val training = combine(
            dao.observeExercises(),
            dao.observeSessions(),
            dao.observeLinks(),
            dao.observeSets(),
        ) { exercises, sessions, links, sets ->
            GymSnapshot(
                exercises = exercises.map { it.toDomain() },
                sessions = sessions.map { it.toDomain() },
                links = links.map { SessionLink(it.sessionId, it.exerciseId, it.position) },
                sets = sets.map { it.toDomain() },
            )
        }
        val routines = combine(
            dao.observeCustomRoutines(),
            dao.observeCustomDays(),
            dao.observeCustomDayExercises(),
        ) { routineRows, dayRows, exerciseRows ->
            routineRows.sortedByDescending { it.createdAt }.map { routine ->
                val days = dayRows.filter { it.routineId == routine.id }.sortedBy { it.position }
                CustomRoutine(
                    id = routine.id,
                    name = routine.name,
                    days = days.map { day ->
                        CustomRoutineDay(
                            id = day.id,
                            title = day.title,
                            isRest = day.isRest,
                            exerciseIds = exerciseRows
                                .filter { it.dayId == day.id }
                                .sortedBy { it.position }
                                .map { it.exerciseId },
                        )
                    },
                )
            }
        }
        return combine(training, routines) { snapshot, custom ->
            snapshot.copy(customRoutines = custom)
        }
    }

    suspend fun startSession(now: Long = System.currentTimeMillis()): Long {
        return dao.insertSession(SessionEntity(startedAt = now))
    }

    suspend fun startSessionWithExercises(
        exerciseNames: List<String>,
        now: Long = System.currentTimeMillis(),
    ): Long {
        ensureSeeded()
        val sessionId = dao.insertSession(SessionEntity(startedAt = now))
        if (exerciseNames.isNotEmpty()) {
            val byName = dao.getAllExercises().associateBy { it.name }
            exerciseNames.forEach { name ->
                val ex = byName[name]
                if (ex != null) {
                    addExerciseToSession(sessionId, ex.id)
                }
            }
        }
        return sessionId
    }

    suspend fun startSessionWithExerciseIds(
        exerciseIds: List<Long>,
        now: Long = System.currentTimeMillis(),
    ): Long {
        ensureSeeded()
        val sessionId = dao.insertSession(SessionEntity(startedAt = now))
        val known = dao.getAllExercises().map { it.id }.toSet()
        exerciseIds.distinct().filter { it in known }.forEach { id ->
            addExerciseToSession(sessionId, id)
        }
        return sessionId
    }

    suspend fun saveCustomRoutine(existingId: Long?, name: String, days: List<RoutineDayDraft>): Long {
        ensureSeeded()
        val routineId = if (existingId == null) {
            dao.insertCustomRoutine(
                CustomRoutineEntity(name = name.trim(), createdAt = System.currentTimeMillis()),
            )
        } else {
            dao.renameCustomRoutine(existingId, name.trim())
            dao.deleteDaysForRoutine(existingId)
            existingId
        }
        days.forEachIndexed { index, day ->
            val dayId = dao.insertCustomDay(
                CustomRoutineDayEntity(
                    routineId = routineId,
                    position = index,
                    title = day.title.trim().ifBlank { if (day.isRest) "Ngày nghỉ" else "Ngày ${index + 1}" },
                    isRest = day.isRest,
                ),
            )
            if (!day.isRest) {
                val links = day.exerciseIds.distinct().mapIndexed { position, exerciseId ->
                    CustomRoutineExerciseEntity(dayId = dayId, exerciseId = exerciseId, position = position)
                }
                if (links.isNotEmpty()) dao.insertCustomDayExercises(links)
            }
        }
        return routineId
    }

    suspend fun deleteCustomRoutine(id: Long) {
        dao.deleteCustomRoutine(id)
    }

    suspend fun createCustomExercise(
        name: String,
        muscle: MuscleGroup,
        repMin: Int = 8,
        repMax: Int = 12,
        incrementKg: Double = 2.5,
        startKg: Double = 20.0,
    ): Long {
        ensureSeeded()
        return dao.insertExercise(
            ExerciseEntity(
                name = name.trim(),
                muscle = muscle.name,
                secondaryMuscles = "",
                repMin = repMin.coerceIn(1, 30),
                repMax = repMax.coerceIn(repMin, 50),
                incrementKg = incrementKg,
                startKg = startKg,
                addedLoad = false,
            ),
        )
    }

    suspend fun finishSession(id: Long, now: Long = System.currentTimeMillis()) {
        dao.finishSession(id, now)
    }

    suspend fun deleteSession(id: Long) {
        dao.deleteSession(id)
    }

    suspend fun addExerciseToSession(sessionId: Long, exerciseId: Long) {
        if (dao.countLink(sessionId, exerciseId) > 0) return
        val position = dao.maxPosition(sessionId) + 1
        dao.insertSessionExercise(
            SessionExerciseEntity(
                sessionId = sessionId,
                exerciseId = exerciseId,
                position = position,
            ),
        )
    }

    suspend fun removeExercise(sessionId: Long, exerciseId: Long) {
        dao.deleteSetsForExercise(sessionId, exerciseId)
        dao.deleteLink(sessionId, exerciseId)
    }

    suspend fun addSet(sessionId: Long, exerciseId: Long, weightKg: Double, reps: Int): Long {
        val number = dao.countSets(sessionId, exerciseId) + 1
        return dao.insertSet(
            SetEntity(
                sessionId = sessionId,
                exerciseId = exerciseId,
                setNumber = number,
                weightKg = normalizeKg(weightKg),
                reps = reps,
                completedAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun updateSet(id: Long, weightKg: Double, reps: Int) {
        dao.updateSet(id, normalizeKg(weightKg), reps)
    }

    suspend fun deleteSet(id: Long) {
        dao.deleteSet(id)
    }

    fun observeMeals(fromMs: Long, toMs: Long): Flow<List<Meal>> {
        return combine(
            dao.observeMealsBetween(fromMs, toMs),
            dao.observeMealItemsBetween(fromMs, toMs),
        ) { meals, items -> assembleMeals(meals, items) }
    }

    suspend fun getMeals(fromMs: Long, toMs: Long): List<Meal> =
        assembleMeals(dao.getMealsBetween(fromMs, toMs), dao.getMealItemsBetween(fromMs, toMs))

    private fun assembleMeals(meals: List<MealEntity>, items: List<MealItemEntity>): List<Meal> {
        val byMeal = items.groupBy { it.mealId }
        return meals.map { meal ->
            Meal(
                id = meal.id,
                eatenAt = meal.eatenAt,
                title = meal.title,
                source = MealSource.fromKey(meal.source),
                confidence = AnalysisConfidence.fromKey(meal.confidence),
                items = byMeal[meal.id].orEmpty().sortedBy { it.position }.map { it.toDomain() },
                photoName = meal.photoName,
            )
        }
    }

    suspend fun saveMeal(
        title: String,
        source: MealSource,
        confidence: AnalysisConfidence?,
        items: List<FoodItem>,
        eatenAt: Long = System.currentTimeMillis(),
        photoName: String? = null,
    ): Long = db.withTransaction {
        val mealId = dao.insertMeal(
            MealEntity(
                eatenAt = eatenAt,
                title = mealTitle(title),
                source = source.key,
                confidence = confidence?.key,
                photoName = photoName,
            ),
        )
        dao.insertMealItems(itemEntities(mealId, items))
        mealId
    }

    /** Rewrites title and items; time, source and photo stay as first logged. */
    suspend fun updateMeal(id: Long, title: String, items: List<FoodItem>) = db.withTransaction {
        dao.updateMealTitle(id, mealTitle(title))
        dao.deleteMealItems(id)
        dao.insertMealItems(itemEntities(id, items))
    }

    private fun mealTitle(title: String) = title.trim().ifBlank { "Bữa ăn" }

    private fun itemEntities(mealId: Long, items: List<FoodItem>) = items.mapIndexed { index, item ->
        MealItemEntity(
            mealId = mealId,
            position = index,
            name = item.name.trim().ifBlank { "Món ${index + 1}" },
            grams = item.grams.coerceIn(0.0, 5000.0),
            kcalPer100g = item.kcalPer100g.coerceIn(0.0, 900.0),
            proteinPer100g = item.proteinPer100g.coerceIn(0.0, 100.0),
            carbsPer100g = item.carbsPer100g.coerceIn(0.0, 100.0),
            fatPer100g = item.fatPer100g.coerceIn(0.0, 100.0),
        )
    }

    suspend fun deleteMeal(id: Long) {
        dao.deleteMeal(id)
    }

    suspend fun exportBackup(): BackupData {
        ensureSeeded()
        return db.withTransaction {
            BackupData(
                exercises = dao.getAllExercises(),
                sessions = dao.getAllSessions(),
                sessionExercises = dao.getAllSessionExercises(),
                sets = dao.getAllSets(),
                customRoutines = dao.getAllCustomRoutines(),
                customRoutineDays = dao.getAllCustomDays(),
                customRoutineExercises = dao.getAllCustomDayExercises(),
                meals = dao.getAllMeals(),
                mealItems = dao.getAllMealItems(),
            )
        }
    }

    /** Replaces every table with the backup; a failed insert rolls the whole restore back. */
    suspend fun restoreBackup(data: BackupData) {
        if (data.exercises.isEmpty()) throw BackupFormatException("Bản sao lưu không có bài tập nào")
        db.withTransaction {
            dao.clearMealItems()
            dao.clearMeals()
            dao.clearSets()
            dao.clearSessionExercises()
            dao.clearSessions()
            dao.clearCustomDayExercises()
            dao.clearCustomDays()
            dao.clearCustomRoutines()
            dao.clearExercises()
            dao.insertExercises(data.exercises)
            dao.insertSessions(data.sessions)
            dao.insertSessionExercises(data.sessionExercises)
            dao.insertSets(data.sets)
            dao.insertCustomRoutines(data.customRoutines)
            dao.insertCustomDays(data.customRoutineDays)
            dao.insertCustomDayExercises(data.customRoutineExercises)
            dao.insertMeals(data.meals)
            dao.insertMealItems(data.mealItems)
        }
    }
}

private fun MealItemEntity.toDomain() = FoodItem(
    name = name,
    grams = grams,
    kcalPer100g = kcalPer100g,
    proteinPer100g = proteinPer100g,
    carbsPer100g = carbsPer100g,
    fatPer100g = fatPer100g,
)

private fun ExerciseEntity.toDomain(): Exercise {
    return Exercise(
        id = id,
        name = name,
        muscle = MuscleGroup.entries.firstOrNull { it.name == muscle } ?: MuscleGroup.CORE,
        secondaryMuscles = parseMuscles(secondaryMuscles),
        repMin = repMin,
        repMax = repMax,
        incrementKg = incrementKg,
        startKg = startKg,
        addedLoad = addedLoad,
    )
}

private fun SessionEntity.toDomain() = Session(id = id, startedAt = startedAt, finishedAt = finishedAt)

private fun SetEntity.toDomain() = SetEntry(
    id = id,
    sessionId = sessionId,
    exerciseId = exerciseId,
    setNumber = setNumber,
    weightKg = weightKg,
    reps = reps,
    completedAt = completedAt,
)
