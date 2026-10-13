package com.example.gymapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ExerciseEntity::class,
        SessionEntity::class,
        SessionExerciseEntity::class,
        SetEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class GymDatabase : RoomDatabase() {
    abstract fun dao(): GymDao
}
