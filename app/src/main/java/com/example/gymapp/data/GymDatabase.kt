package com.example.gymapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ExerciseEntity::class,
        SessionEntity::class,
        SessionExerciseEntity::class,
        SetEntity::class,
        CustomRoutineEntity::class,
        CustomRoutineDayEntity::class,
        CustomRoutineExerciseEntity::class,
        MealEntity::class,
        MealItemEntity::class,
    ],
    version = 5,
    exportSchema = true,
)
abstract class GymDatabase : RoomDatabase() {
    abstract fun dao(): GymDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_session_exercises_exerciseId` ON `session_exercises` (`exerciseId`)",
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `custom_routines` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)",
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `custom_routine_days` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `routineId` INTEGER NOT NULL, `position` INTEGER NOT NULL, `title` TEXT NOT NULL, `isRest` INTEGER NOT NULL, FOREIGN KEY(`routineId`) REFERENCES `custom_routines`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_custom_routine_days_routineId` ON `custom_routine_days` (`routineId`)",
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `custom_routine_exercises` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `dayId` INTEGER NOT NULL, `exerciseId` INTEGER NOT NULL, `position` INTEGER NOT NULL, FOREIGN KEY(`dayId`) REFERENCES `custom_routine_days`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, FOREIGN KEY(`exerciseId`) REFERENCES `exercises`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_custom_routine_exercises_dayId` ON `custom_routine_exercises` (`dayId`)",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_custom_routine_exercises_exerciseId` ON `custom_routine_exercises` (`exerciseId`)",
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `meals` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `eatenAt` INTEGER NOT NULL, `title` TEXT NOT NULL, `source` TEXT NOT NULL, `confidence` TEXT)",
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `meal_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `mealId` INTEGER NOT NULL, `position` INTEGER NOT NULL, `name` TEXT NOT NULL, `grams` REAL NOT NULL, `kcalPer100g` REAL NOT NULL, `proteinPer100g` REAL NOT NULL, `carbsPer100g` REAL NOT NULL, `fatPer100g` REAL NOT NULL, FOREIGN KEY(`mealId`) REFERENCES `meals`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_meal_items_mealId` ON `meal_items` (`mealId`)",
                )
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `meals` ADD COLUMN `photoName` TEXT")
            }
        }
    }
}
