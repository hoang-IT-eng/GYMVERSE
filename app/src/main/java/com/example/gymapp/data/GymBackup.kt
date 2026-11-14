package com.example.gymapp.data

import com.example.gymapp.domain.MuscleGroup
import com.example.gymapp.domain.epley1Rm
import com.example.gymapp.domain.formatKg
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class BackupData(
    val exercises: List<ExerciseEntity>,
    val sessions: List<SessionEntity>,
    val sessionExercises: List<SessionExerciseEntity>,
    val sets: List<SetEntity>,
    val customRoutines: List<CustomRoutineEntity>,
    val customRoutineDays: List<CustomRoutineDayEntity>,
    val customRoutineExercises: List<CustomRoutineExerciseEntity>,
    val meals: List<MealEntity> = emptyList(),
    val mealItems: List<MealItemEntity> = emptyList(),
)

class BackupFormatException(message: String, cause: Throwable? = null) : Exception(message, cause)

object GymBackup {
    private const val FORMAT = "ironlog-backup"
    private const val VERSION = 2

    fun toJson(data: BackupData, exportedAt: Long = System.currentTimeMillis()): String {
        val root = JSONObject()
            .put("format", FORMAT)
            .put("version", VERSION)
            .put("exportedAt", exportedAt)
            .put("exercises", data.exercises.toJsonArray { e ->
                JSONObject()
                    .put("id", e.id)
                    .put("name", e.name)
                    .put("muscle", e.muscle)
                    .put("secondaryMuscles", e.secondaryMuscles)
                    .put("repMin", e.repMin)
                    .put("repMax", e.repMax)
                    .put("incrementKg", e.incrementKg)
                    .put("startKg", e.startKg)
                    .put("addedLoad", e.addedLoad)
            })
            .put("sessions", data.sessions.toJsonArray { s ->
                JSONObject()
                    .put("id", s.id)
                    .put("startedAt", s.startedAt)
                    .put("finishedAt", s.finishedAt ?: JSONObject.NULL)
            })
            .put("sessionExercises", data.sessionExercises.toJsonArray { l ->
                JSONObject()
                    .put("id", l.id)
                    .put("sessionId", l.sessionId)
                    .put("exerciseId", l.exerciseId)
                    .put("position", l.position)
            })
            .put("sets", data.sets.toJsonArray { s ->
                JSONObject()
                    .put("id", s.id)
                    .put("sessionId", s.sessionId)
                    .put("exerciseId", s.exerciseId)
                    .put("setNumber", s.setNumber)
                    .put("weightKg", s.weightKg)
                    .put("reps", s.reps)
                    .put("completedAt", s.completedAt)
            })
            .put("customRoutines", data.customRoutines.toJsonArray { r ->
                JSONObject()
                    .put("id", r.id)
                    .put("name", r.name)
                    .put("createdAt", r.createdAt)
            })
            .put("customRoutineDays", data.customRoutineDays.toJsonArray { d ->
                JSONObject()
                    .put("id", d.id)
                    .put("routineId", d.routineId)
                    .put("position", d.position)
                    .put("title", d.title)
                    .put("isRest", d.isRest)
            })
            .put("customRoutineExercises", data.customRoutineExercises.toJsonArray { x ->
                JSONObject()
                    .put("id", x.id)
                    .put("dayId", x.dayId)
                    .put("exerciseId", x.exerciseId)
                    .put("position", x.position)
            })
            .put("meals", data.meals.toJsonArray { m ->
                JSONObject()
                    .put("id", m.id)
                    .put("eatenAt", m.eatenAt)
                    .put("title", m.title)
                    .put("source", m.source)
                    .put("confidence", m.confidence ?: JSONObject.NULL)
            })
            .put("mealItems", data.mealItems.toJsonArray { i ->
                JSONObject()
                    .put("id", i.id)
                    .put("mealId", i.mealId)
                    .put("position", i.position)
                    .put("name", i.name)
                    .put("grams", i.grams)
                    .put("kcalPer100g", i.kcalPer100g)
                    .put("proteinPer100g", i.proteinPer100g)
                    .put("carbsPer100g", i.carbsPer100g)
                    .put("fatPer100g", i.fatPer100g)
            })
        return root.toString(2)
    }

    fun fromJson(text: String): BackupData {
        val root = try {
            JSONObject(text)
        } catch (e: Exception) {
            throw BackupFormatException("File không phải JSON hợp lệ", e)
        }
        if (root.optString("format") != FORMAT) {
            throw BackupFormatException("File không phải bản sao lưu IronLog")
        }
        val version = root.optInt("version", -1)
        if (version > VERSION || version < 1) {
            throw BackupFormatException("Phiên bản sao lưu $version không được hỗ trợ")
        }
        return try {
            BackupData(
                exercises = root.getJSONArray("exercises").mapObjects { o ->
                    ExerciseEntity(
                        id = o.getLong("id"),
                        name = o.getString("name"),
                        muscle = o.getString("muscle"),
                        secondaryMuscles = o.optString("secondaryMuscles", ""),
                        repMin = o.getInt("repMin"),
                        repMax = o.getInt("repMax"),
                        incrementKg = o.getDouble("incrementKg"),
                        startKg = o.getDouble("startKg"),
                        addedLoad = o.getBoolean("addedLoad"),
                    )
                },
                sessions = root.getJSONArray("sessions").mapObjects { o ->
                    SessionEntity(
                        id = o.getLong("id"),
                        startedAt = o.getLong("startedAt"),
                        finishedAt = if (o.isNull("finishedAt")) null else o.getLong("finishedAt"),
                    )
                },
                sessionExercises = root.getJSONArray("sessionExercises").mapObjects { o ->
                    SessionExerciseEntity(
                        id = o.getLong("id"),
                        sessionId = o.getLong("sessionId"),
                        exerciseId = o.getLong("exerciseId"),
                        position = o.getInt("position"),
                    )
                },
                sets = root.getJSONArray("sets").mapObjects { o ->
                    SetEntity(
                        id = o.getLong("id"),
                        sessionId = o.getLong("sessionId"),
                        exerciseId = o.getLong("exerciseId"),
                        setNumber = o.getInt("setNumber"),
                        weightKg = o.getDouble("weightKg"),
                        reps = o.getInt("reps"),
                        completedAt = o.getLong("completedAt"),
                    )
                },
                customRoutines = root.optJSONArray("customRoutines").mapObjects { o ->
                    CustomRoutineEntity(
                        id = o.getLong("id"),
                        name = o.getString("name"),
                        createdAt = o.getLong("createdAt"),
                    )
                },
                customRoutineDays = root.optJSONArray("customRoutineDays").mapObjects { o ->
                    CustomRoutineDayEntity(
                        id = o.getLong("id"),
                        routineId = o.getLong("routineId"),
                        position = o.getInt("position"),
                        title = o.getString("title"),
                        isRest = o.getBoolean("isRest"),
                    )
                },
                customRoutineExercises = root.optJSONArray("customRoutineExercises").mapObjects { o ->
                    CustomRoutineExerciseEntity(
                        id = o.getLong("id"),
                        dayId = o.getLong("dayId"),
                        exerciseId = o.getLong("exerciseId"),
                        position = o.getInt("position"),
                    )
                },
                meals = root.optJSONArray("meals").mapObjects { o ->
                    MealEntity(
                        id = o.getLong("id"),
                        eatenAt = o.getLong("eatenAt"),
                        title = o.getString("title"),
                        source = o.getString("source"),
                        confidence = if (o.isNull("confidence")) null else o.getString("confidence"),
                    )
                },
                mealItems = root.optJSONArray("mealItems").mapObjects { o ->
                    MealItemEntity(
                        id = o.getLong("id"),
                        mealId = o.getLong("mealId"),
                        position = o.getInt("position"),
                        name = o.getString("name"),
                        grams = o.getDouble("grams"),
                        kcalPer100g = o.getDouble("kcalPer100g"),
                        proteinPer100g = o.getDouble("proteinPer100g"),
                        carbsPer100g = o.getDouble("carbsPer100g"),
                        fatPer100g = o.getDouble("fatPer100g"),
                    )
                },
            )
        } catch (e: BackupFormatException) {
            throw e
        } catch (e: Exception) {
            throw BackupFormatException("Bản sao lưu bị thiếu hoặc sai dữ liệu", e)
        }
    }

    /** One row per logged set; UTF-8 BOM so Excel shows Vietnamese names correctly. */
    fun toCsv(data: BackupData, zone: ZoneId = ZoneId.systemDefault()): String {
        val exercises = data.exercises.associateBy { it.id }
        val startedAt = data.sessions.associate { it.id to it.startedAt }
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.US).withZone(zone)
        val rows = data.sets.sortedWith(
            compareBy<SetEntity>({ startedAt[it.sessionId] ?: it.completedAt }, { it.completedAt }),
        )
        return buildString {
            append('\uFEFF')
            appendLine("ngay,buoi_tap,bai_tap,nhom_co,hiep,ta_kg,reps,khoi_luong_kg,e1rm_kg")
            rows.forEach { set ->
                val exercise = exercises[set.exerciseId]
                val muscle = MuscleGroup.entries.firstOrNull { it.name == exercise?.muscle }?.label.orEmpty()
                val e1rm = epley1Rm(set.weightKg, set.reps)
                val cells = listOf(
                    dateFormat.format(Instant.ofEpochMilli(set.completedAt)),
                    set.sessionId.toString(),
                    exercise?.name.orEmpty(),
                    muscle,
                    set.setNumber.toString(),
                    formatKg(set.weightKg),
                    set.reps.toString(),
                    formatKg(set.weightKg * set.reps),
                    e1rm?.let { String.format(Locale.US, "%.1f", it) }.orEmpty(),
                )
                appendLine(cells.joinToString(",") { csvCell(it) })
            }
        }
    }

    private fun csvCell(value: String): String {
        if (value.none { it == ',' || it == '"' || it == '\n' || it == '\r' }) return value
        return "\"" + value.replace("\"", "\"\"") + "\""
    }

    private inline fun <T> List<T>.toJsonArray(transform: (T) -> JSONObject): JSONArray {
        val array = JSONArray()
        forEach { array.put(transform(it)) }
        return array
    }

    private inline fun <T> JSONArray?.mapObjects(transform: (JSONObject) -> T): List<T> {
        if (this == null) return emptyList()
        return (0 until length()).map { transform(getJSONObject(it)) }
    }
}
