package com.example.gymapp

import android.app.Application
import androidx.room.Room
import com.example.gymapp.data.GymDatabase
import com.example.gymapp.data.GymRepository
import com.example.gymapp.service.RestTimerController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class GymApp : Application() {
    lateinit var repository: GymRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = Room.databaseBuilder(this, GymDatabase::class.java, "gym.db")
            .addMigrations(GymDatabase.MIGRATION_1_2, GymDatabase.MIGRATION_2_3)
            .fallbackToDestructiveMigration()
            .build()
        repository = GymRepository(database.dao())
        RestTimerController.ensureChannels(this)
        RestTimerController.restorePhase(this)
    }

    companion object {
        val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}
