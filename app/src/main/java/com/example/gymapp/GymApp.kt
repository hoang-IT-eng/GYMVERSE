package com.example.gymapp

import android.app.Application
import androidx.room.Room
import com.example.gymapp.data.GeminiClient
import com.example.gymapp.data.GeminiMealAnalyzer
import com.example.gymapp.data.GymDatabase
import com.example.gymapp.data.GymRepository
import com.example.gymapp.data.MealPhotoStore
import com.example.gymapp.data.WeeklyCoach
import com.example.gymapp.service.RestTimerController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class GymApp : Application() {
    lateinit var repository: GymRepository
        private set
    lateinit var mealPhotos: MealPhotoStore
        private set
    private val gemini = GeminiClient(BuildConfig.GEMINI_API_KEY, BuildConfig.GEMINI_MODEL)
    val mealAnalyzer = GeminiMealAnalyzer(gemini)
    val coach = WeeklyCoach(gemini)

    override fun onCreate() {
        super.onCreate()
        val database = Room.databaseBuilder(this, GymDatabase::class.java, "gym.db")
            .addMigrations(
                GymDatabase.MIGRATION_1_2,
                GymDatabase.MIGRATION_2_3,
                GymDatabase.MIGRATION_3_4,
                GymDatabase.MIGRATION_4_5,
            )
            .build()
        repository = GymRepository(database)
        mealPhotos = MealPhotoStore(this)
        RestTimerController.ensureChannels(this)
        RestTimerController.restorePhase(this)
    }

    companion object {
        val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}
